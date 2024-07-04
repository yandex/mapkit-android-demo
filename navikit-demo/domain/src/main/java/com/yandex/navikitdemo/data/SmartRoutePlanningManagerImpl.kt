package com.yandex.navikitdemo.data

import android.util.Log
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.FilterCollection
import com.yandex.mapkit.search.FilterCollectionUtils
import com.yandex.navikitdemo.domain.SearchManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SmartRoutePlanningManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.mapper.SmartRouteStateMapper
import com.yandex.navikitdemo.domain.models.DrivingSessionState
import com.yandex.navikitdemo.domain.models.SearchState
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.runtime.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartRoutePlanningManagerImpl @Inject constructor(
    private val vehicleOptionsManager: VehicleOptionsManager,
    private val settingsManager: SettingsManager,
    private val searchManager: SearchManager,
    private val smartRouteStateMapper: SmartRouteStateMapper,
) : SmartRoutePlanningManager {

    private val drivingRouter =
        DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
    private var drivingSession: DrivingSession? = null
    private val drivingSessionState = MutableStateFlow<DrivingSessionState>(DrivingSessionState.Off)

    private val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
        override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
            val fastestDrivingRoute = drivingRoutes[0]
            drivingSessionState.value = DrivingSessionState.Success(fastestDrivingRoute)
        }

        override fun onDrivingRoutesError(error: Error) {
            drivingSessionState.value = DrivingSessionState.Error
        }

    }

    override val routeState: Flow<SmartRouteState> = drivingSessionState.map {
        smartRouteStateMapper.mapDrivingStateToRouteState(it) { drivingRoute ->
            getRequestPoints(drivingRoute)
        }
    }.distinctUntilChanged()

    override fun requestRoutes(points: List<RequestPoint>) {
        Log.i(TAG, "requestSmartRoute")
        val vehicleOptions = vehicleOptionsManager.vehicleOptions()
        val fromToPoints =
            points.filterIndexed { index, _ -> index == 0 || index == points.lastIndex }
        searchManager.reset()
        drivingSession?.cancel()
        drivingSession = drivingRouter.requestRoutes(
            fromToPoints,
            DRIVING_OPTIONS,
            vehicleOptions,
            drivingRouteListener
        )
        drivingSessionState.value = DrivingSessionState.Loading
    }

    override fun retry() {
        Log.i(TAG, "retry SmartRoute")
        if (settingsManager.smartRoutePlanningEnabled.value) {
            drivingSession?.retry(drivingRouteListener)
        }
    }

    override fun reset() {
        searchManager.reset()
        drivingSession?.cancel()
        drivingSession = null
        drivingSessionState.value = DrivingSessionState.Off
    }

    private suspend fun getRequestPoints(drivingRoute: DrivingRoute): List<RequestPoint>? {
        val thresholdPoints = getThresholdPoints(drivingRoute) ?: return null
        val filterType = settingsManager.chargingType.value.filter
        val fuelConnectorType = settingsManager.fuelConnectorType.value.type
        val filter = FilterCollectionUtils.createFilterCollectionBuilder()
            .also { it.addEnumFilter(filterType, listOf(fuelConnectorType)) }.build()
        val viaPoints = thresholdPoints.map { getViaStationPoint(it, filter) ?: return null }
        Log.i(TAG, "thresholdSize ${thresholdPoints.size} viaPointsSize: ${viaPoints.size}")
        val fromToPoints = drivingRoute.requestPoints.orEmpty()
        val requestPoints = createRequestPoints(fromToPoints, viaPoints)
        return requestPoints
    }

    private fun getThresholdPoints(drivingRoute: DrivingRoute): List<Point>? {
        val thresholdDistance = settingsManager.thresholdDistance.value.toMeters()
        val maxTravelDistance =
            settingsManager.maxTravelDistance.value.toMeters() - thresholdDistance
        var estimatedRange = settingsManager.currentRangeLvl.value.toMeters() - thresholdDistance

        if (maxTravelDistance <= 0 || estimatedRange <= 0) return null

        Log.i(
            TAG,
            "thresholdDistance: ${thresholdDistance / 1000} km " +
                    "estimatedRange: ${estimatedRange / 1000} km " +
                    "maxTravelDistance: ${maxTravelDistance / 1000} km " +
                    "fullDistance: ${drivingRoute.metadata.weight.distance.text}"
        )

        val thresholdPoints = mutableListOf<Point>()
        val currentPosition = drivingRoute.routePosition
        val fullRouteDistance = drivingRoute.metadata.weight.distance.value
        while (estimatedRange < fullRouteDistance) {
            val targetPosition = currentPosition.advance(estimatedRange)
            val sectionPoint = targetPosition.point
            thresholdPoints.add(sectionPoint)
            Log.i(TAG, "estimatedRange: ${estimatedRange / 1000} km ")
            Log.i(TAG, "sectionPoint: ${sectionPoint.latitude}, ${sectionPoint.longitude}")
            estimatedRange += maxTravelDistance
        }
        Log.i(TAG, "thresholdPoints: ${thresholdPoints.size} ")
        return thresholdPoints
    }

    private suspend fun getViaStationPoint(
        thresholdPoint: Point,
        filter: FilterCollection
    ): Point? {
        val query = settingsManager.chargingType.value.vehicle
        val thresholdDistance = settingsManager.thresholdDistance.value.toMeters()
        searchManager.submitSearch(query, thresholdPoint, filter)
        val searchPoint = searchManager.searchState
            .filter { it is SearchState.Success || it is SearchState.Error }
            .firstOrNull()
            ?.run { (this as? SearchState.Success)?.searchPoints }
            ?.firstOrNull {
                val distanceToViaStation = Geo.distance(thresholdPoint, it)
                Log.i(TAG, "Via: ${it.latitude},${it.longitude}")
                Log.i(
                    TAG, "distanceToViaPoint " +
                            "${String.format("%.1f", distanceToViaStation / 1000)} km"
                )
                distanceToViaStation <= thresholdDistance
            }
        searchPoint ?: Log.e(TAG, "ViaStationPoint - Error!")
        return searchPoint
    }

    private fun createRequestPoints(
        fromToPoints: List<RequestPoint>,
        viaPoints: List<Point>,
    ): List<RequestPoint> {
        val from = fromToPoints.firstOrNull() ?: return emptyList()
        val to = fromToPoints.lastOrNull() ?: return emptyList()
        return buildList {
            add(from)
            addAll(viaPoints.map { it.toRequestPoint(RequestPointType.WAYPOINT) })
            add(to)
        }
    }

    private fun Float.toMeters() = this * 1000.0

    private fun Point.toRequestPoint(type: RequestPointType): RequestPoint {
        return RequestPoint(this, type, null, null)
    }

    private companion object {

        const val TAG = "SmartRouteManagerImpl"
        val DRIVING_OPTIONS: DrivingOptions = DrivingOptions().setRoutesCount(1)
    }

}
package com.yandex.navikitdemo.data

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.PolylinePosition
import com.yandex.mapkit.geometry.Segment
import com.yandex.mapkit.geometry.Subpolyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.geometry.geo.PolylineIndex
import com.yandex.mapkit.geometry.geo.PolylineUtils
import com.yandex.mapkit.search.FilterCollection
import com.yandex.mapkit.search.FilterCollectionUtils
import com.yandex.navikitdemo.domain.SearchManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SmartRoutePlanningManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.mapper.SmartRouteStateMapper
import com.yandex.navikitdemo.domain.models.FuelConnectorType
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.navikitdemo.domain.models.State
import com.yandex.navikitdemo.domain.utils.advancePositionOnRoute
import com.yandex.navikitdemo.domain.utils.distanceLeft
import com.yandex.navikitdemo.domain.utils.ifNotNull
import com.yandex.navikitdemo.domain.utils.toMeters
import com.yandex.navikitdemo.domain.utils.toRequestPoint
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
    private val drivingSessionState = MutableStateFlow<State<DrivingRoute>>(State.Off)

    private val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
        override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
            drivingSessionState.value =
                drivingRoutes.firstOrNull()?.let { State.Success(it) } ?: State.Error
        }

        override fun onDrivingRoutesError(error: Error) {
            drivingSessionState.value = State.Error
        }

    }

    override val routeState: Flow<SmartRouteState> = drivingSessionState.map {
        smartRouteStateMapper.mapDrivingStateToRouteState(it) { drivingRoute ->
            getRequestPoints(drivingRoute)
        }
    }.distinctUntilChanged()

    override fun requestRoutes(points: List<RequestPoint>) {
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
        drivingSessionState.value = State.Loading
    }

    override fun retry() {
        if (settingsManager.smartRoutePlanningEnabled.value) {
            drivingSession?.retry(drivingRouteListener)
        }
    }

    override fun reset() {
        searchManager.reset()
        drivingSession?.cancel()
        drivingSession = null
        drivingSessionState.value = State.Off
    }

    private suspend fun getRequestPoints(drivingRoute: DrivingRoute): List<RequestPoint>? {
        val viaPoints = getViaPoints(drivingRoute) ?: return null
        val fromToPoints = drivingRoute.requestPoints.orEmpty()
        val requestPoints = createRequestPoints(fromToPoints, viaPoints)
        return requestPoints
    }

    private suspend fun getViaPoints(drivingRoute: DrivingRoute): List<Point>? {
        val routeGeometry = drivingRoute.geometry
        val fullRouteDistance = drivingRoute.distanceLeft().value
        val thresholdDistance = settingsManager.thresholdDistance.value.toMeters()
        val maxTravelDistance =
            settingsManager.maxTravelDistance.value.toMeters() - thresholdDistance
        val currentRange = settingsManager.currentRangeLvl.value.toMeters()
        val sectionRange = SectionRange(to = currentRange)
        val query = settingsManager.chargingType.value.vehicle
        val filter = settingsManager.fuelConnectorType.value.filterTypeCollection()
        val viaPoints = mutableListOf<Point>()

        while (sectionRange.to < fullRouteDistance) {
            val startPosition = drivingRoute.advancePositionOnRoute(sectionRange.from)
            val targetPosition = drivingRoute.advancePositionOnRoute(sectionRange.to)
            val sectionPolyline = routeGeometry.sectionSubpolyline(startPosition, targetPosition)
            val viaPoint = getViaStationPoint(sectionPolyline, query, filter)
                ?.takeIf { viaPoints.isEmpty() || Geo.distance(it, viaPoints.last()) > 0 }
                ?: return null
            val closestPosition = sectionPolyline?.closestPolylinePosition(routeGeometry, viaPoint)
            val remainingDistance = ifNotNull(closestPosition, targetPosition) { a, b ->
                PolylineUtils.distanceBetweenPolylinePositions(routeGeometry, a, b)
            } ?: return null
            sectionRange.appendRange(remainingDistance, maxTravelDistance)
            viaPoints.add(viaPoint)
        }
        return viaPoints
    }

    private suspend fun getViaStationPoint(
        polyline: Polyline?,
        query: String,
        filter: FilterCollection
    ): Point? {
        val thresholdPoint = polyline?.points?.lastOrNull() ?: return null
        searchManager.submitSearch(query, polyline, filter)
        val searchPoint = searchManager.searchState
            .filter { it is State.Success || it is State.Error }
            .firstOrNull()
            ?.let { (it as? State.Success)?.data }
            ?.minByOrNull { Geo.distance(thresholdPoint, it) }
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
            addAll(viaPoints.map { it.toRequestPoint() })
            add(to)
        }
    }

    private fun FuelConnectorType.filterTypeCollection(): FilterCollection {
        return FilterCollectionUtils.createFilterCollectionBuilder()
            .also { it.addEnumFilter(chargingType.filter, listOf(type)) }
            .build()
    }

    private fun Polyline.closestPolylinePosition(
        routePolyline: Polyline,
        viaPoint: Point
    ): PolylinePosition? {
        return points
            .zipWithNext { a, b -> Segment(a, b) }
            .map { Geo.closestPoint(viaPoint, it) }
            .minByOrNull { Geo.distance(viaPoint, it) }
            ?.let {
                val polylineIndex = PolylineUtils.createPolylineIndex(routePolyline)
                polylineIndex.closestPolylinePosition(
                    it,
                    PolylineIndex.Priority.CLOSEST_TO_RAW_POINT,
                    1.0
                )
            }
    }

    private fun Polyline.sectionSubpolyline(
        begin: PolylinePosition?,
        end: PolylinePosition?
    ): Polyline? {
        val sectionSubpolyline = ifNotNull(begin, end) { a, b ->
            val subpolyline = Subpolyline(a, b)
            if (a.segmentIndex < b.segmentIndex)
                SubpolylineHelper.subpolyline(this, subpolyline)
            else
                null
        }
        return sectionSubpolyline
    }

    private data class SectionRange(var from: Double = 0.0, var to: Double = 0.0) {

        fun appendRange(remainingDistance: Double, expectedRange: Double): SectionRange {
            from = to - remainingDistance
            to += expectedRange - remainingDistance
            return this
        }

    }

    private companion object {

        val DRIVING_OPTIONS: DrivingOptions = DrivingOptions().setRoutesCount(1)
    }

}

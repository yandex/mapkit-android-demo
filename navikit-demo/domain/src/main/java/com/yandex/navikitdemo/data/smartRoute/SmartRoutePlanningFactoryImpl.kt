package com.yandex.navikitdemo.data.smartRoute

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.PolylinePosition
import com.yandex.mapkit.geometry.Segment
import com.yandex.mapkit.geometry.Subpolyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.geometry.geo.PolylineIndex
import com.yandex.mapkit.geometry.geo.PolylineUtils
import com.yandex.navikitdemo.domain.models.SectionRange
import com.yandex.navikitdemo.domain.models.SmartRouteOptions
import com.yandex.navikitdemo.domain.smartRoute.SmartRoutePlanningFactory
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteSearchFactory
import com.yandex.navikitdemo.domain.utils.advancePositionOnRoute
import com.yandex.navikitdemo.domain.utils.distanceLeft
import com.yandex.navikitdemo.domain.utils.requestRoutes
import com.yandex.navikitdemo.domain.utils.toRequestPoint
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import kotlin.Error

class SmartRoutePlanningFactoryImpl @Inject constructor(
    private val routeSearchFactory: SmartRouteSearchFactory
) : SmartRoutePlanningFactory {

    override suspend fun requestRoutes(
        from: RequestPoint,
        to: RequestPoint,
        vehicleOptions: VehicleOptions,
        smartRouteOptions: SmartRouteOptions
    ): Result<List<RequestPoint>> {
        val router = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        val points = listOf(from, to)
        val drivingOptions = DrivingOptions().setRoutesCount(1)
        val drivingRoute =
            router.requestRoutes(points, drivingOptions, vehicleOptions).firstOrNull()?.getOrNull()
                ?: return Result.failure(Error("DrivingRoutes error"))
        val viaPoints = getViaPoints(drivingRoute, smartRouteOptions)
            ?: return Result.failure(Error("ViaPoints error"))
        val fromToPoints = drivingRoute.requestPoints.orEmpty()
        val requestPoints = createRequestPoints(fromToPoints, viaPoints)
        return Result.success(requestPoints)
    }

    private suspend fun getViaPoints(
        drivingRoute: DrivingRoute,
        smartRouteOptions: SmartRouteOptions
    ): List<Point>? {
        val routeGeometry = drivingRoute.geometry
        val fullRouteDistance = drivingRoute.distanceLeft().value
        val thresholdDistance = smartRouteOptions.thresholdDistanceInMeters
        val maxTravelDistance = smartRouteOptions.maxTravelDistanceInMeters - thresholdDistance
        val currentRange = smartRouteOptions.currentRangeLvlInMeters
        val sectionRange = SectionRange(to = currentRange)
        val viaPoints = mutableListOf<Point>()

        while (sectionRange.to < fullRouteDistance) {
            val startPosition = drivingRoute.advancePositionOnRoute(sectionRange.from)
            val targetPosition = drivingRoute.advancePositionOnRoute(sectionRange.to)
            if (startPosition == null || targetPosition == null) return null

            val sectionPolyline = routeGeometry.sectionSubpolyline(startPosition, targetPosition)
                ?: return null
            val viaPoint = getViaForPolyline(sectionPolyline, viaPoints, smartRouteOptions)
                ?: return null
            val closestPosition = sectionPolyline.closestPolylinePosition(routeGeometry, viaPoint)
                ?: return null

            val remainingDistance = PolylineUtils.distanceBetweenPolylinePositions(
                routeGeometry,
                closestPosition,
                targetPosition
            )
            sectionRange.appendRange(remainingDistance, maxTravelDistance)
            viaPoints.add(viaPoint)
        }
        return viaPoints
    }

    private suspend fun getViaForPolyline(
        sectionPolyline: Polyline,
        viaPoints: List<Point>,
        smartRouteOptions: SmartRouteOptions
    ): Point? {
        val thresholdPoint = sectionPolyline.points.lastOrNull() ?: return null
        return routeSearchFactory.getViaForPolyline(
            thresholdPoint,
            sectionPolyline,
            smartRouteOptions
        ).getOrNull()
            ?.takeIf { viaPoints.isEmpty() || Geo.distance(it, viaPoints.last()) > 0 }
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
        begin: PolylinePosition,
        end: PolylinePosition
    ): Polyline? {
        val subpolyline = Subpolyline(begin, end)
        val sectionSubpolyline = if (begin.segmentIndex < end.segmentIndex) {
            SubpolylineHelper.subpolyline(this, subpolyline)
        } else {
            null
        }
        return sectionSubpolyline
    }

}

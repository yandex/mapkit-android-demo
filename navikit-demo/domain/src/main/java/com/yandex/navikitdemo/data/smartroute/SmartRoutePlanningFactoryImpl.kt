package com.yandex.navikitdemo.data.smartroute

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
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
import com.yandex.navikitdemo.domain.models.SmartRouteOptions
import com.yandex.navikitdemo.domain.smartroute.SmartRoutePlanningFactory
import com.yandex.navikitdemo.domain.smartroute.SmartRouteSearchFactory
import com.yandex.navikitdemo.domain.utils.advancePositionOnRoute
import com.yandex.navikitdemo.domain.utils.requestRoutes
import com.yandex.navikitdemo.domain.utils.toRequestPoint
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SmartRoutePlanningFactoryImpl @Inject constructor(
    private val routeSearchFactory: SmartRouteSearchFactory,
    private val drivingRouter: DrivingRouter
) : SmartRoutePlanningFactory {

    override suspend fun requestRoutes(
        from: RequestPoint,
        to: RequestPoint,
        vehicleOptions: VehicleOptions,
        smartRouteOptions: SmartRouteOptions
    ): Result<List<RequestPoint>> {
        val fromToPoints = listOf(from, to)
        val drivingOptions = DrivingOptions().setRoutesCount(1)
        val drivingRoute =
            drivingRouter.requestRoutes(fromToPoints, drivingOptions, vehicleOptions).firstOrNull()
                ?.getOrNull()
                ?: return Result.failure(Exception("DrivingRoutes error"))
        val viaPoints = getViaPoints(drivingRoute, smartRouteOptions)
            ?: return Result.failure(Exception("ViaPoints error"))
        val requestPoints = createRequestPoints(from, viaPoints, to)
        return Result.success(requestPoints)
    }

    private suspend fun getViaPoints(
        drivingRoute: DrivingRoute,
        smartRouteOptions: SmartRouteOptions
    ): List<Point>? {
        val routeGeometry = drivingRoute.geometry
        val maxTravelDistance =
            smartRouteOptions.maxTravelDistanceInMeters - smartRouteOptions.thresholdDistanceInMeters
        var currentRange = smartRouteOptions.currentRangeLvlInMeters
        val viaPoints = mutableListOf<Point>()
        while (drivingRoute.routePosition.distanceToFinish() > currentRange) {
            val currentPosition = drivingRoute.position
            val targetPosition = drivingRoute.advancePositionOnRoute(currentRange) ?: return null

            val sectionPolyline = routeGeometry.sectionSubpolyline(currentPosition, targetPosition)
                ?: return null
            val viaPoint = getViaForPolyline(sectionPolyline, viaPoints.lastOrNull(), smartRouteOptions)
                ?: return null
            val closestPosition = sectionPolyline.closestPolylinePosition(routeGeometry, viaPoint)
                ?: return null

            drivingRoute.position = closestPosition
            currentRange = maxTravelDistance
            viaPoints.add(viaPoint)
        }
        return viaPoints
    }

    private suspend fun getViaForPolyline(
        sectionPolyline: Polyline,
        lastChargingPoint: Point?,
        smartRouteOptions: SmartRouteOptions
    ): Point? {
        val thresholdPoint = sectionPolyline.points.lastOrNull() ?: return null
        return routeSearchFactory.getViaForPolyline(
            thresholdPoint,
            sectionPolyline,
            smartRouteOptions
        ).getOrNull()
            ?.takeIf { lastChargingPoint == null || Geo.distance(it, lastChargingPoint) > 1 }
    }

    private fun createRequestPoints(
        from: RequestPoint,
        viaPoints: List<Point>,
        to: RequestPoint,
    ): List<RequestPoint> {
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

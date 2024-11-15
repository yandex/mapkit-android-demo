package com.yandex.navikitdemo.data.smartroute

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.search.SearchManager
import com.yandex.navikitdemo.domain.models.SmartRouteOptions
import com.yandex.navikitdemo.domain.smartroute.SmartRoutePoint
import com.yandex.navikitdemo.domain.smartroute.SmartRouteResult
import com.yandex.navikitdemo.domain.smartroute.SmartRoutePlanningFactory
import javax.inject.Inject

class SmartRoutePlanningFactoryImpl @Inject constructor(
    searchManager: SearchManager,
    drivingRouter: DrivingRouter,
) : SmartRoutePlanningFactory {

    private val smartRoutePlanningFactory = SimpleSmartRoutePlanningFactoryImpl(searchManager, drivingRouter)

    override suspend fun requestRoutes(
        points: List<RequestPoint>,
        smartRouteOptions: SmartRouteOptions
    ): SmartRouteResult {
        if (points.size <= 1) {
            return SmartRouteResult.Error("Cant build smart route with less than 2 points")
        }

        val result = mutableListOf<SmartRoutePoint>()
        result.add(SmartRoutePoint.RegularPoint(points.first()))

        var options = smartRouteOptions

        for (i in 0 until points.size - 1) {
            val smartResult = smartRoutePlanningFactory.requestRoutes(
                points[i],
                points[i + 1],
                options
            )
            if (smartResult is SmartRouteResult.Success) {
                result.addAll(smartResult.points.subList(1, smartResult.points.size))
                options = smartRouteOptions.copy(
                    currentRangeLvlInMeters = smartResult.finishRangeLvlInMeters
                )
            } else {
                return smartResult
            }
        }

        return SmartRouteResult.Success(result, options.currentRangeLvlInMeters)
    }
}

package com.yandex.navikitdemo.domain.smartroute

import com.yandex.mapkit.RequestPoint
import com.yandex.navikitdemo.domain.models.SmartRouteOptions

interface SmartRoutePlanningFactory {

    suspend fun requestRoutes(
        points: List<RequestPoint>,
        smartRouteOptions: SmartRouteOptions
    ): SmartRouteResult
}

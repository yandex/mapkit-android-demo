package com.yandex.navikitdemo.domain.smartroute

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.navikitdemo.domain.models.SmartRouteOptions

interface SmartRoutePlanningFactory {

    suspend fun requestRoutes(
        from: RequestPoint,
        to: RequestPoint,
        vehicleOptions: VehicleOptions,
        smartRouteOptions: SmartRouteOptions
    ): Result<List<RequestPoint>>
}

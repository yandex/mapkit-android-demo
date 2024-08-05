package com.yandex.navikitdemo.domain.mapper

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.navikitdemo.domain.models.State

interface SmartRouteStateMapper {

    suspend fun mapDrivingStateToRouteState(
        drivingSessionState: State<DrivingRoute>,
        requestPoints: suspend (DrivingRoute) -> List<RequestPoint>?
    ): SmartRouteState

}

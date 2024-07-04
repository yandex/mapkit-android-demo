package com.yandex.navikitdemo.domain.mapper

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.navikitdemo.domain.models.DrivingSessionState
import com.yandex.navikitdemo.domain.models.SmartRouteState

interface SmartRouteStateMapper {

    suspend fun mapDrivingStateToRouteState(
        drivingSessionState: DrivingSessionState,
        requestPoints: suspend (DrivingRoute) -> List<RequestPoint>?
    ): SmartRouteState

}
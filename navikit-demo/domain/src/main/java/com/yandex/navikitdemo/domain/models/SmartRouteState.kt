package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingRoute

sealed interface SmartRouteState {
    object Off : SmartRouteState
    object Loading : SmartRouteState
    object Error : SmartRouteState
    data class Success(
        val requestPoints: List<RequestPoint>,
    ) : SmartRouteState
}

sealed interface DrivingSessionState {
    object Off : DrivingSessionState
    object Loading : DrivingSessionState
    object Error : DrivingSessionState
    data class Success(
        val drivingRoute: DrivingRoute,
    ) : DrivingSessionState
}

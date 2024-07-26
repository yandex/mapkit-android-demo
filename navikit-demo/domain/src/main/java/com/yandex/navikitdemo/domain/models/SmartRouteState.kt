package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.RequestPoint

sealed interface SmartRouteState {
    object Off : SmartRouteState
    object Loading : SmartRouteState
    object Error : SmartRouteState
    data class Success(
        val requestPoints: List<RequestPoint>,
    ) : SmartRouteState
}

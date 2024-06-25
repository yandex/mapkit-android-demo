package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Point

sealed interface SmartRouteState {
    object Off : SmartRouteState
    object Loading : SmartRouteState
    object Error : SmartRouteState
    data class Success(
        val requestPoints: List<RequestPoint>,
    ) : SmartRouteState
}

sealed interface SearchState {
    object Off : SearchState
    object Loading : SearchState
    object Error : SearchState
    data class Success(
        val searchPoints: List<Point>,
    ) : SearchState
}
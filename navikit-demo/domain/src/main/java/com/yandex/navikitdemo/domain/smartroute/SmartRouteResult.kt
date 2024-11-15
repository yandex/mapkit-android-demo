package com.yandex.navikitdemo.domain.smartroute

sealed class SmartRouteResult {
    data class Success(
        val points: List<SmartRoutePoint>,
        val finishRangeLvlInMeters: Double
    ) : SmartRouteResult()

    data class Error(
        val reason: String,
    ) : SmartRouteResult()
}

package com.yandex.navikitdemo.domain.smartRoute

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.navikitdemo.domain.models.SmartRouteOptions

interface SmartRouteSearchFactory {

    suspend fun getViaForPolyline(
        thresholdPoint: Point,
        polyline: Polyline,
        options: SmartRouteOptions
    ): Result<Point>

}

package com.yandex.navikitdemo.domain.smartroute

import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Point

sealed class SmartRoutePoint {
    abstract val point: RequestPoint
    data class ChargingPoint(override val point: RequestPoint, val geoObject: GeoObject) : SmartRoutePoint()
    data class RegularPoint(override val point: RequestPoint) : SmartRoutePoint()
}

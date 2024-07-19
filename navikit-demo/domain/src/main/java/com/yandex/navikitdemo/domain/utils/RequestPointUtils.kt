package com.yandex.navikitdemo.domain.utils

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point

fun Point.toRequestPoint(type: RequestPointType = RequestPointType.WAYPOINT): RequestPoint {
    return RequestPoint(this, type, null, null)
}

package com.yandex.navigationdemo.domain

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow

interface RequestPointsManager {
    val requestPoints: Flow<List<RequestPoint>>

    fun setFromPoint(point: Point)
    fun setToPoint(point: Point)
    fun addViaPoint(point: Point)

    fun resetPoints()
}

package com.yandex.navikitdemo.domain.helpers

import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface MapTapManager {
    val longTapActions: Flow<Point>
    fun start(scope: CoroutineScope)
}

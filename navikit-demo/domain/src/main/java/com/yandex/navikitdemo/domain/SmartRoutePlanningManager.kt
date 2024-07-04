package com.yandex.navikitdemo.domain

import com.yandex.mapkit.RequestPoint
import com.yandex.navikitdemo.domain.models.SmartRouteState
import kotlinx.coroutines.flow.Flow

interface SmartRoutePlanningManager {

    val routeState: Flow<SmartRouteState>

    fun requestRoutes(points: List<RequestPoint>)

    fun retry()

    fun reset()
}

package com.yandex.navikitdemo.domain.smartRoute

import com.yandex.mapkit.RequestPoint
import com.yandex.navikitdemo.domain.models.SmartRouteState
import kotlinx.coroutines.flow.Flow

interface SmartRoutePlanningManager {

    fun requestRoutes(from: RequestPoint, to: RequestPoint): SmartRoutePlanningSession

}


interface SmartRoutePlanningSession {

    val routeState: Flow<SmartRouteState>

    fun retry()

    fun reset()

}

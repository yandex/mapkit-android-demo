package com.yandex.navikitdemo.data.smartRoute

import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteDrivingListener
import com.yandex.navikitdemo.domain.smartRoute.SmartRoutePlanningSession
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteSearchSession
import kotlinx.coroutines.flow.MutableStateFlow

internal class SmartRoutePlanningSessionBinding(
    private var drivingSession: DrivingSession? = null,
    private var drivingRouteListener: SmartRouteDrivingListener,
    internal var smartRouteSearchSession: SmartRouteSearchSession? = null
) : SmartRoutePlanningSession {

    override val routeState = MutableStateFlow<SmartRouteState>(SmartRouteState.Off)

    override fun retry() {
        drivingSession?.retry(drivingRouteListener)
    }

    override fun reset() {
        drivingSession?.cancel()
        smartRouteSearchSession?.cancel()
        drivingRouteListener.onDrivingRoutesReset()
        drivingSession = null
        smartRouteSearchSession = null
    }

}
package com.yandex.navikitdemo.domain.helpers

import com.yandex.mapkit.navigation.automotive.GuidanceListener
import com.yandex.mapkit.navigation.automotive.RouteChangeReason

abstract class SimpleGuidanceListener : GuidanceListener {
    override fun onLocationChanged() = Unit
    override fun onCurrentRouteChanged(p0: RouteChangeReason) = Unit
    override fun onRouteLost() = Unit
    override fun onReturnedToRoute() = Unit
    override fun onRouteFinished() = Unit
    override fun onWayPointReached() = Unit
    override fun onStandingStatusChanged() = Unit
    override fun onRoadNameChanged() = Unit
    override fun onSpeedLimitUpdated() = Unit
    override fun onSpeedLimitStatusUpdated() = Unit
    override fun onAlternativesChanged() = Unit
    override fun onFastestAlternativeChanged() = Unit
}

package com.yandex.navikitdemo.domain.smartRoute

import com.yandex.mapkit.directions.driving.DrivingSession

interface SmartRouteDrivingListener : DrivingSession.DrivingRouteListener {

    fun onDrivingRoutesReset()
}
package com.yandex.navikitdemo.domain.mapper

import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.navikitdemo.domain.models.State

interface NavigationRouteStateMapper {

    fun mapSmartRouteStateToRouteState(smartRouteState: SmartRouteState): State<List<DrivingRoute>>

}

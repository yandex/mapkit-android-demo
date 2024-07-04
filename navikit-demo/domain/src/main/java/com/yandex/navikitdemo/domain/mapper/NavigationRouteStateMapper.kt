package com.yandex.navikitdemo.domain.mapper

import com.yandex.navikitdemo.domain.models.NavigationRouteState
import com.yandex.navikitdemo.domain.models.SmartRouteState

interface NavigationRouteStateMapper {

    fun mapSmartRouteStateToRouteState(smartRouteState: SmartRouteState): NavigationRouteState?

}
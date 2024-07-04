package com.yandex.navikitdemo.data.mapper

import com.yandex.navikitdemo.domain.mapper.NavigationRouteStateMapper
import com.yandex.navikitdemo.domain.models.NavigationRouteState
import com.yandex.navikitdemo.domain.models.SmartRouteState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRouteStateMapperImpl @Inject constructor() : NavigationRouteStateMapper {

    override fun mapSmartRouteStateToRouteState(smartRouteState: SmartRouteState): NavigationRouteState? {
        return when (smartRouteState) {
            SmartRouteState.Error -> NavigationRouteState.Error
            is SmartRouteState.Success,
            SmartRouteState.Loading -> NavigationRouteState.Loading

            SmartRouteState.Off -> null
        }
    }

}

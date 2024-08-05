package com.yandex.navikitdemo.data.mapper

import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.navikitdemo.domain.mapper.NavigationRouteStateMapper
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.navikitdemo.domain.models.State
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRouteStateMapperImpl @Inject constructor() : NavigationRouteStateMapper {

    override fun mapSmartRouteStateToRouteState(smartRouteState: SmartRouteState): State<List<DrivingRoute>> {
        return when (smartRouteState) {
            is SmartRouteState.Success,
            SmartRouteState.Loading -> State.Loading

            SmartRouteState.Error -> State.Error
            SmartRouteState.Off -> State.Off
        }
    }

}

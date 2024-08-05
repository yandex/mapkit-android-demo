package com.yandex.navikitdemo.data.mapper

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.navikitdemo.domain.mapper.SmartRouteStateMapper
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.navikitdemo.domain.models.State
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartRouteStateMapperImpl @Inject constructor() : SmartRouteStateMapper {

    override suspend fun mapDrivingStateToRouteState(
        drivingSessionState: State<DrivingRoute>,
        requestPoints: suspend (DrivingRoute) -> List<RequestPoint>?
    ): SmartRouteState {
        return when (drivingSessionState) {
            State.Off -> SmartRouteState.Off
            State.Loading -> SmartRouteState.Loading
            State.Error -> SmartRouteState.Error
            is State.Success -> requestPoints.invoke(drivingSessionState.data)
                ?.let { SmartRouteState.Success(it) }
                ?: SmartRouteState.Error
        }
    }

}

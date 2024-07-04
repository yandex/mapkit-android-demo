package com.yandex.navikitdemo.data.mapper

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.navikitdemo.domain.mapper.SmartRouteStateMapper
import com.yandex.navikitdemo.domain.models.DrivingSessionState
import com.yandex.navikitdemo.domain.models.SmartRouteState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartRouteStateMapperImpl @Inject constructor() : SmartRouteStateMapper {

    override suspend fun mapDrivingStateToRouteState(
        drivingSessionState: DrivingSessionState,
        requestPoints: suspend (DrivingRoute) -> List<RequestPoint>?
    ): SmartRouteState {
        return when (drivingSessionState) {
            DrivingSessionState.Off -> SmartRouteState.Off
            DrivingSessionState.Loading -> SmartRouteState.Loading
            DrivingSessionState.Error -> SmartRouteState.Error
            is DrivingSessionState.Success -> requestPoints.invoke(drivingSessionState.drivingRoute)
                ?.let { SmartRouteState.Success(it) }
                ?: SmartRouteState.Error
        }
    }

}

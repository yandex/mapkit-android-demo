package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.directions.driving.DrivingRoute

sealed interface NavigationState {
    object Off : NavigationState
    object Loading : NavigationState
    object Error : NavigationState
    data class Success(val data: List<DrivingRoute>) : NavigationState
}

package com.yandex.navikitdemo.domain.models

sealed interface NavigationRouteState {
    object Off : NavigationRouteState
    object Loading : NavigationRouteState
    object Error : NavigationRouteState
    object Success : NavigationRouteState
}
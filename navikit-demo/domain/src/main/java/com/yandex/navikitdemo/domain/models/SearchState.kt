package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.geometry.Point

sealed interface SearchState {
    object Off : SearchState
    object Loading : SearchState
    object Error : SearchState
    data class Success(
        val searchPoints: List<Point>,
    ) : SearchState
}
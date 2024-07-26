package com.yandex.navikitdemo.domain.models

sealed interface State<out T> {
    object Off : State<Nothing>
    object Loading : State<Nothing>
    object Error : State<Nothing>
    data class Success<out T>(val data: T) : State<T>
}

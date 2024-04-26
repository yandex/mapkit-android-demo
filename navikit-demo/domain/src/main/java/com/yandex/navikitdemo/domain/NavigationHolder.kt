package com.yandex.navikitdemo.domain

import com.yandex.mapkit.navigation.automotive.Navigation
import kotlinx.coroutines.flow.StateFlow

interface NavigationHolder {
    val navigation: StateFlow<Navigation>

    fun serialize()
    fun deserialize()
}

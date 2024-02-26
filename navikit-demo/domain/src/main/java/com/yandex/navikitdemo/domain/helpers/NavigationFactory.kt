package com.yandex.navikitdemo.domain.helpers

import com.yandex.mapkit.navigation.automotive.Navigation

interface NavigationFactory {
    fun wasDeserializedFirstTime(): Boolean
    fun create(): Navigation
}

package com.yandex.navikitdemo.domain.helpers

import com.yandex.mapkit.navigation.automotive.Navigation

interface NavigationDeserializer {
    fun deserializeNavigationFromSettings(): Navigation?
}

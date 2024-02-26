package com.yandex.navikitdemo.domain

import com.yandex.mapkit.location.Location
import kotlinx.coroutines.flow.StateFlow

interface LocationManager {
    fun location(): StateFlow<Location?>
}

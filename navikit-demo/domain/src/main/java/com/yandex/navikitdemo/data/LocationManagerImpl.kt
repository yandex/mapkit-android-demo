package com.yandex.navikitdemo.data

import com.yandex.mapkit.navigation.automotive.Guidance
import com.yandex.mapkit.location.Location
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.helpers.SimpleGuidanceListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

private val LOCATION_UPDATE_TIMEOUT = 1.seconds

@Singleton
class LocationManagerImpl @Inject constructor(
    private val guidance: Guidance,
) : LocationManager, SimpleGuidanceListener() {

    private val locationImpl = MutableStateFlow<Location?>(null)
    private var lastLocationTime: Long = 0

    private val guidanceListener = object : SimpleGuidanceListener() {
        override fun onLocationChanged() {
            if ((System.currentTimeMillis() - lastLocationTime).seconds < LOCATION_UPDATE_TIMEOUT) return
            lastLocationTime = System.currentTimeMillis()
            locationImpl.value = guidance.location
        }
    }

    init {
        guidance.addListener(guidanceListener)
    }

    override fun location(): StateFlow<Location?> = locationImpl
}

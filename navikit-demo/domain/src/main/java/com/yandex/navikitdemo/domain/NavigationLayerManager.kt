package com.yandex.navikitdemo.domain

import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.navigation.guidance_camera.CameraMode
import com.yandex.mapkit.road_events.EventTag
import kotlinx.coroutines.flow.StateFlow

interface NavigationLayerManager {
    val selectedRoute: DrivingRoute?
    var cameraMode: CameraMode

    val maneuverBalloonVisibility: StateFlow<Boolean>
    val cameraFollowingMode: StateFlow<Boolean>

    fun initIfNeeded()
    fun recreateNavigationLayer()
    fun refreshStyle()

    fun setShowBalloonsGeometry(show: Boolean)
    fun setSwitchModesAutomatically(enabled: Boolean)
    fun setAutoRotation(enabled: Boolean)
    fun setAutoZoom(enabled: Boolean)
    fun setFollowingModeZoomOffset(offset: Float)
    fun setOverviewRect(rect: ScreenRect)
    fun setRoadEventVisibleOnRoute(tag: EventTag, visible: Boolean)
}

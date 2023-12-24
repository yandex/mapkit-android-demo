package com.yandex.navigationdemo.data

import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.navigation.guidance_camera.CameraMode
import com.yandex.navigationdemo.domain.CameraManager
import com.yandex.navigationdemo.domain.LocationManager
import com.yandex.navigationdemo.domain.NavigationLayerManager
import com.yandex.navigationdemo.domain.SettingsManager
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val MAP_ZOOM_STEP = 1f
private const val MAP_DEFAULT_ZOOM = 15f
private const val ZOOM_OFFSET_MAX_VALUE = 5f
private const val ZOOM_OFFSET_MIN_VALUE = -10f

@ActivityScoped
class CameraManagerImpl @Inject constructor(
    private val map: com.yandex.mapkit.map.Map,
    private val navigationLayerManager: NavigationLayerManager,
    private val locationManager: LocationManager,
    private val settingsManager: SettingsManager,
) : CameraManager {

    private var isLocationUnknown = true

    override fun moveCameraToUserLocation() {
        val location = locationManager.location().value ?: return
        map.cameraPosition.run {
            map.move(
                CameraPosition(location.position, MAP_DEFAULT_ZOOM, azimuth, 0f),
                Animation(Animation.Type.SMOOTH, 0.5f),
                null
            )
        }
    }

    override fun start(scope: CoroutineScope) {
        subscribeFirstLocationObtained().launchIn(scope)
    }

    override fun changeZoomByStep(step: CameraManager.ZoomStep) {
        val stepValue = when (step) {
            CameraManager.ZoomStep.PLUS -> MAP_ZOOM_STEP
            CameraManager.ZoomStep.MINUS -> -MAP_ZOOM_STEP
        }
        if (navigationLayerManager.cameraMode == CameraMode.FOLLOWING && settingsManager.autoZoom.value) {
            // When in following we need to use zoomOffset setting.
            // Otherwise, CameraMode changes and following turns off.
            settingsManager.zoomOffset.value = (settingsManager.zoomOffset.value + stepValue)
                .coerceIn(ZOOM_OFFSET_MIN_VALUE, ZOOM_OFFSET_MAX_VALUE)
        } else {
            changeCameraZoomByStepImpl(stepValue)
        }
    }

    private fun changeCameraZoomByStepImpl(step: Float) {
        map.cameraPosition.run {
            map.move(
                CameraPosition(target, zoom + step, azimuth, tilt),
                Animation(Animation.Type.LINEAR, 0.2f),
                null
            )
        }
    }

    private fun subscribeFirstLocationObtained(): Flow<*> {
        return locationManager.location()
            .filterNotNull()
            .filter { isLocationUnknown }
            .onEach {
                isLocationUnknown = false
                moveCameraToUserLocation()
            }
    }
}

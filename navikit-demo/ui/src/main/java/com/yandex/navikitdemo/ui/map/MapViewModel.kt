package com.yandex.navikitdemo.ui.map

import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.helpers.NavigationFactory
import com.yandex.navikitdemo.domain.isGuidanceActive
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val navigationFactory: NavigationFactory,
    private val settingsManager: SettingsManager,
    private val navigationManager: NavigationManager,
    private val requestPointsManager: RequestPointsManager,
) : ViewModel() {

    fun isGuidanceInProgress(): Boolean {
        return navigationFactory.wasDeserializedFirstTime()
            || navigationManager.isGuidanceActive
    }

    fun clearNavigationSerialization() {
        if (settingsManager.restoreGuidanceState.value) {
            settingsManager.serializedNavigation.value = ""
        }
    }

    fun setToPoint(point: Point) {
        requestPointsManager.setToPoint(point)
    }
}

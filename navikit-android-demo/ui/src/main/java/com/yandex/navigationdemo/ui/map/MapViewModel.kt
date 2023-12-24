package com.yandex.navigationdemo.ui.map

import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.navigationdemo.domain.NavigationManager
import com.yandex.navigationdemo.domain.RequestPointsManager
import com.yandex.navigationdemo.domain.SettingsManager
import com.yandex.navigationdemo.domain.helpers.NavigationFactory
import com.yandex.navigationdemo.domain.isGuidanceActive
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
        settingsManager.serializedNavigation.value = ""
    }

    fun setToPoint(point: Point) {
        requestPointsManager.setToPoint(point)
    }
}

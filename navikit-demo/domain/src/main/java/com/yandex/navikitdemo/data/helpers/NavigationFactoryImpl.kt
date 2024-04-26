package com.yandex.navikitdemo.data.helpers

import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.navigation.automotive.Navigation
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.helpers.NavigationDeserializer
import com.yandex.navikitdemo.domain.helpers.NavigationFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationFactoryImpl @Inject constructor(
    private val settingsManager: SettingsManager,
    private val navigationDeserializer: NavigationDeserializer,
) : NavigationFactory {

    private var wasDeserializedImpl = false

    override fun wasDeserializedFirstTime(): Boolean {
        val value = wasDeserializedImpl
        wasDeserializedImpl = false
        return value
    }

    /**
     * Recreates Navigation from the serialized data, otherwise creates a new instance.
     */
    override fun create(): Navigation {
        if (settingsManager.restoreGuidanceState.value) {
            val navigation = navigationDeserializer.deserializeNavigationFromSettings()
            if (navigation != null) {
                wasDeserializedImpl = true
                settingsManager.serializedNavigation.value = ""
                return navigation
            }
        }

        return com.yandex.mapkit.navigation.automotive.NavigationFactory.createNavigation(
            DrivingRouterType.COMBINED
        )
    }
}

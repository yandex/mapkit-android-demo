package com.yandex.navikitdemo.ui.settings.settingslist

import com.yandex.mapkit.navigation.automotive.SpeedLimitsPolicy
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.SettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SpeedLimitsInteractor @Inject constructor(
    private val navigationManager: NavigationManager,
    settingsManager: SettingsManager,
) {
    val speedLimitsPolicy: SpeedLimitsPolicy
        get() = navigationManager.speedLimitsPolicy

    val speedLimitsTolerance: Double
        get() = navigationManager.speedLimitTolerance

    val viewStateChanges: Flow<Unit> = settingsManager.speedLimitTolerance.changes().map {  }
}

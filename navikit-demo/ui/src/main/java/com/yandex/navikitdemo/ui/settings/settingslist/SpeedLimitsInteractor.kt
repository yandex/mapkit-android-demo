package com.yandex.navikitdemo.ui.settings.settingslist

import com.yandex.mapkit.navigation.automotive.Guidance
import com.yandex.mapkit.navigation.automotive.SpeedLimitsPolicy
import com.yandex.navikitdemo.domain.SettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SpeedLimitsInteractor @Inject constructor(
    private val guidance: Guidance,
    settingsManager: SettingsManager,
) {
    val speedLimitsPolicy: SpeedLimitsPolicy
        get() = guidance.speedLimitsPolicy

    val speedLimitsTolerance: Double
        get() = guidance.speedLimitTolerance

    val viewStateChanges: Flow<Unit> = settingsManager.speedLimitTolerance.changes().map {  }
}

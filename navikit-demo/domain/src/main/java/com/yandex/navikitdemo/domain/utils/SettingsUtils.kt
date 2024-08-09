package com.yandex.navikitdemo.domain.utils

import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.models.SmartRouteOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun SettingsManager.smartRouteOptionsChanges(): Flow<SmartRouteOptions?> {
    return combine(
        smartRoutePlanningEnabled.changes(),
        fuelConnectorTypes.changes(),
        maxTravelDistance.changes(),
        currentRangeLvl.changes(),
        thresholdDistance.changes(),
    ) { smartRoutePlanningEnabled, _, _, _, _ ->
        if (smartRoutePlanningEnabled) {
            smartRouteOptions()
        } else {
            null
        }
    }
}

fun SettingsManager.smartRouteOptions(): SmartRouteOptions {
    return SmartRouteOptions(
        chargingType.value,
        fuelConnectorTypes.value,
        maxTravelDistance.value.toMeters(),
        currentRangeLvl.value.toMeters(),
        thresholdDistance.value.toMeters()
    )
}

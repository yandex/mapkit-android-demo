package com.yandex.navikitdemo.domain.models

import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.utils.toMeters

data class SmartRouteOptions(
    val chargingType: ChargingType,
    val fuelConnectorTypes: Set<FuelConnectorType>,
    val maxTravelDistanceInMeters: Double,
    val currentRangeLvlInMeters: Double,
    val thresholdDistanceInMeters: Double,
) {

    constructor(settingsManager: SettingsManager) : this(
        settingsManager.chargingType.value,
        settingsManager.fuelConnectorTypes.value,
        settingsManager.maxTravelDistance.value.toMeters(),
        settingsManager.currentRangeLvl.value.toMeters(),
        settingsManager.thresholdDistance.value.toMeters()
    )

}

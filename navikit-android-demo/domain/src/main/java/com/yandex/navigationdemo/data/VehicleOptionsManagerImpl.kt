package com.yandex.navigationdemo.data

import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.navigationdemo.domain.SettingsManager
import com.yandex.navigationdemo.domain.VehicleOptionsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleOptionsManagerImpl @Inject constructor(
    private val settingsManager: SettingsManager,
) : VehicleOptionsManager {

    override fun vehicleOptions(): VehicleOptions {
        if (settingsManager.vehicleType.value == VehicleType.DEFAULT) return VehicleOptions()
        return VehicleOptions().apply {
            vehicleType = settingsManager.vehicleType.value
            weight = settingsManager.weight.value
            axleWeight = settingsManager.axleWeight.value
            maxWeight = settingsManager.maxWeight.value
            height = settingsManager.height.value
            width = settingsManager.width.value
            length = settingsManager.length.value
            payload = settingsManager.payload.value
            ecoClass = settingsManager.ecoClass.value.number
            hasTrailer = settingsManager.hasTrailer.value
            buswayPermitted = settingsManager.buswayPermitted.value
        }
    }
}

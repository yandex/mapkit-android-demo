package com.yandex.navikitdemo.data.smartroute

import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.models.SmartRouteOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class SmartRouteSettingsProvider @Inject constructor(
    private val settingsManager: SettingsManager,
    private val vehicleOptionsManager: VehicleOptionsManager,
) {
    fun changes(): Flow<SmartRouteOptions> {
        return with(settingsManager) {
            combine(
                fuelConnectorTypes.changes(),
                maxTravelDistance.changes(),
                currentRangeLvl.changes(),
                thresholdDistance.changes(),
                avoidTolls.changes(),
                avoidUnpaved.changes(),
                avoidPoorConditions.changes()
            ) { _ ->
                smartRouteOptions()
            }
        }
    }
    private fun SettingsManager.smartRouteOptions(): SmartRouteOptions {
        return SmartRouteOptions(
            chargingType.value,
            fuelConnectorTypes.value,
            maxTravelDistance.value.toMeters(),
            currentRangeLvl.value.toMeters(),
            thresholdDistance.value.toMeters(),
            DrivingOptions().also { options ->
                options.avoidTolls = avoidTolls.value
                options.avoidUnpaved = avoidUnpaved.value
                options.avoidPoorConditions = avoidPoorConditions.value
            },
            vehicleOptionsManager.vehicleOptions(),
        )
    }

    private fun Float.toMeters(): Double = this * 1000.0
}

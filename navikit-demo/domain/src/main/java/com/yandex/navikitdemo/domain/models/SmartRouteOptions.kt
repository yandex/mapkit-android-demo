package com.yandex.navikitdemo.domain.models

import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.VehicleOptions

data class SmartRouteOptions(
    val chargingType: ChargingType,
    val fuelConnectorTypes: Set<FuelConnectorType>,
    val maxTravelDistanceInMeters: Double,
    val currentRangeLvlInMeters: Double,
    val thresholdDistanceInMeters: Double,
    val drivingOptions: DrivingOptions,
    val vehicleOptions: VehicleOptions,
)

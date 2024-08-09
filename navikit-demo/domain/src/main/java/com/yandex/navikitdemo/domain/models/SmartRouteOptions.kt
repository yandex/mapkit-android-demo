package com.yandex.navikitdemo.domain.models

data class SmartRouteOptions(
    val chargingType: ChargingType,
    val fuelConnectorTypes: Set<FuelConnectorType>,
    val maxTravelDistanceInMeters: Double,
    val currentRangeLvlInMeters: Double,
    val thresholdDistanceInMeters: Double,
)

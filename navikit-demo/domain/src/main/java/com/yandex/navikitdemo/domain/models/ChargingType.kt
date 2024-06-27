package com.yandex.navikitdemo.domain.models

enum class ChargingType(val vehicle: String, val filter: String) {
    ELECTRIC("electric_car_charging_station", "plugtype"),
    GAS("gas_station", "fuel"),
}
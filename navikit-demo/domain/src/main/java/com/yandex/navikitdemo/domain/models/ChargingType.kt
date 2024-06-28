package com.yandex.navikitdemo.domain.models

enum class ChargingType(val vehicle: String, val filter: String) {
    ELECTRO("electric_car_charging_station", "plugtype"),
    GASOLINE("gas_station", "fuel"),
}
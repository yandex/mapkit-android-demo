package com.yandex.navikitdemo.domain.models

enum class ChargingType(
    val displayName: String,
    val vehicle: String,
    val filter: String
) {

    ELECTRO("Electro", "electric_car_charging_station", "plugtype"),
    GASOLINE("Gasoline", "gas_station", "fuel")

}

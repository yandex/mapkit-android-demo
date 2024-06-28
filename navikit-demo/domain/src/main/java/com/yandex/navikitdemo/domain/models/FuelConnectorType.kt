package com.yandex.navikitdemo.domain.models

enum class FuelConnectorType(val type: String, val chargingType: ChargingType) {
    //ChargingType.GAS
    A_100("a100", ChargingType.GASOLINE),
    A_95("a_95", ChargingType.GASOLINE),
    A_92("a_92", ChargingType.GASOLINE),
    A_80("a_80", ChargingType.GASOLINE),

    //ChargingType.ELECTRIC
    TYPE_1("ev_plug_j1772", ChargingType.ELECTRO),
    TYPE_2("ev_plug2", ChargingType.ELECTRO),
    TYPE_3C("ev_plug3", ChargingType.ELECTRO),
    CHADEMO("CHADEMO", ChargingType.ELECTRO),
}

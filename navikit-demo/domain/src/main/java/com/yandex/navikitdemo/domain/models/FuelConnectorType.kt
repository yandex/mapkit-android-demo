package com.yandex.navikitdemo.domain.models

import com.yandex.navikitdemo.domain.models.ChargingType.ELECTRO
import com.yandex.navikitdemo.domain.models.ChargingType.GASOLINE

enum class FuelConnectorType(val type: String, val chargingType: ChargingType = GASOLINE) {

    //GASOLINE
    A_100_PREMIUM("a100_premium"),
    A_100("a100"),
    A_98_PREMIUM("a98_premium"),
    A_98("a_98"),
    A_95_PREMIUM("a95_premium"),
    A_95("a_95"),
    A_92_PREMIUM("a92_premium"),
    A_92("a_92"),
    A_80_PREMIUM("a80_premium"),
    A_80("a_80"),
    DIESEL_PREMIUM("diesel_premium"),
    DIESEL("gm"),
    PROPANE("lpg"),

    //ELECTRO
    TYPE_1("ev_plug_j1772", ELECTRO),
    TYPE_2("ev_plug2", ELECTRO),
    CCS_COMBO_1("ccs_combo_1", ELECTRO),
    CCS_COMBO_2("ccs_combo", ELECTRO),
    CHADEMO("chademo_dcfc", ELECTRO),
    GB_T_DC("gbt_dc", ELECTRO),
    GB_T_AC("gbt_ac", ELECTRO),
    WALL_OUTLET("wall_outlet_europlug", ELECTRO);

    companion object {

        fun getConnectorByChargingType(chargingType: ChargingType): List<FuelConnectorType> {
            return enumValues<FuelConnectorType>().filter { it.chargingType == chargingType }
        }
    }

}


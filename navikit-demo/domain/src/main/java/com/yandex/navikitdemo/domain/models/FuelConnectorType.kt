package com.yandex.navikitdemo.domain.models

import com.yandex.navikitdemo.domain.models.ChargingType.ELECTRO
import com.yandex.navikitdemo.domain.models.ChargingType.GASOLINE

enum class FuelConnectorType(
    val displayName: String,
    val type: String,
    val chargingType: ChargingType = GASOLINE
) {

    //GASOLINE
    A_100_PREMIUM("AI-100 Premium", "a100_premium"),
    A_100("AI-100", "a100"),
    A_98_PREMIUM("AI-98 Premium", "a98_premium"),
    A_98("AI-98", "a_98"),
    A_95_PREMIUM("AI-95 Premium", "a95_premium"),
    A_95("AI-95", "a_95"),
    A_92_PREMIUM("AI-92 Premium", "a92_premium"),
    A_92("AI-92", "a_92"),
    A_80_PREMIUM("AI-80 Premium", "a80_premium"),
    A_80("AI-80", "a_80"),
    DIESEL_PREMIUM("Diesel Premium", "diesel_premium"),
    DIESEL("Diesel", "gm"),
    PROPANE("Propane", "lpg"),

    //ELECTRO
    TYPE_1("Type 1", "ev_plug_j1772", ELECTRO),
    TYPE_2("Type 2", "ev_plug2", ELECTRO),
    CCS_COMBO_1("CCS Combo 1", "ccs_combo_1", ELECTRO),
    CCS_COMBO_2("CCS Combo 2", "ccs_combo", ELECTRO),
    CHADEMO("CHAdeMO", "chademo_dcfc", ELECTRO),
    GB_T_DC("GB/T DC", "gbt_dc", ELECTRO),
    GB_T_AC("GB/T AC", "gbt_ac", ELECTRO),
    WALL_OUTLET("Wall Outlet", "wall_outlet_europlug", ELECTRO);

    companion object {

        fun getConnectorByChargingType(chargingType: ChargingType): List<FuelConnectorType> {
            return enumValues<FuelConnectorType>().filter { it.chargingType == chargingType }
        }
    }

}


package com.yandex.navikitdemo.ui.settings.settingslist

import android.content.Context
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.models.ChargingType
import com.yandex.navikitdemo.domain.models.EcoClass
import com.yandex.navikitdemo.domain.models.FuelConnectorType
import com.yandex.navikitdemo.domain.models.JamsMode
import com.yandex.navikitdemo.domain.models.StyleMode
import com.yandex.navikitdemo.ui.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class CheckListType {
    JAMS,
    VEHICLE_TYPE,
    ECO_CLASS,
    ANNOTATION_LANGUAGE,
    STYLE_MODE,
    CHARGING_TYPE,
}

data class CheckListState(
    val title: String,
    val selected: String,
    val options: List<String>,
)

class CheckListInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager
) {

    fun viewState(settingType: CheckListType): CheckListState {
        return when (settingType) {
            CheckListType.JAMS -> CheckListState(
                context.getString(R.string.settings_checklist_jams_mode),
                settingsManager.jamsMode.value.toString(),
                JamsMode.values().map { it.toString() }
            )

            CheckListType.VEHICLE_TYPE -> CheckListState(
                context.getString(R.string.settings_checklist_vehicle_type),
                settingsManager.vehicleType.value.toString(),
                VehicleType.values().map { it.toString() }
            )

            CheckListType.ECO_CLASS -> CheckListState(
                context.getString(R.string.settings_checklist_eco_class),
                settingsManager.ecoClass.value.toString(),
                EcoClass.values().map { it.toString() }
            )

            CheckListType.ANNOTATION_LANGUAGE -> CheckListState(
                context.getString(R.string.settings_checklist_annotation_language),
                settingsManager.annotationLanguage.value.toString(),
                AnnotationLanguage.values().map { it.toString() }
            )

            CheckListType.STYLE_MODE -> CheckListState(
                context.getString(R.string.settings_checklist_style_mode),
                settingsManager.styleMode.value.toString(),
                StyleMode.values().map { it.toString() }
            )

            CheckListType.CHARGING_TYPE -> CheckListState(
                context.getString(R.string.settings_checklist_car_type),
                settingsManager.chargingType.value.displayName,
                ChargingType.values().map { it.displayName }
            )
        }
    }

    fun <T : Enum<T>> onMenuItemClicked(index: Int, settingType: CheckListType) {
        when (settingType) {
            CheckListType.JAMS -> settingsManager.jamsMode.value = JamsMode.values()[index]
            CheckListType.VEHICLE_TYPE -> settingsManager.vehicleType.value =
                VehicleType.values()[index]

            CheckListType.ECO_CLASS -> settingsManager.ecoClass.value = EcoClass.values()[index]
            CheckListType.ANNOTATION_LANGUAGE -> settingsManager.annotationLanguage.value =
                AnnotationLanguage.values()[index]

            CheckListType.STYLE_MODE -> settingsManager.styleMode.value =
                StyleMode.values()[index]

            CheckListType.CHARGING_TYPE -> {
                settingsManager.chargingType.value = ChargingType.values()[index]
                val fuelConnectorType =
                    FuelConnectorType.getConnectorByChargingType(settingsManager.chargingType.value)
                        .first()
                settingsManager.fuelConnectorTypes.value = setOf(fuelConnectorType)
            }
        }
    }
}

package com.yandex.navikitdemo.ui.settings.settingslist

import android.content.Context
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.models.FuelConnectorType
import com.yandex.navikitdemo.ui.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class MultipleCheckListType {
    FUEL_CONNECTOR_TYPES,
}

data class MultipleCheckListState(
    val title: String,
    val selected: List<String>,
    val options: List<String>,
)

class MultipleCheckListInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager
) {

    fun viewState(settingType: MultipleCheckListType): MultipleCheckListState {
        return when (settingType) {
            MultipleCheckListType.FUEL_CONNECTOR_TYPES -> MultipleCheckListState(
                context.getString(R.string.settings_checklist_fuel_connector_type),
                settingsManager.fuelConnectorTypes.value.map { it.displayName },
                FuelConnectorType.getConnectorByChargingType(settingsManager.chargingType.value)
                    .map { it.displayName }
            )
        }
    }

    fun onMenuItemClicked(index: Int, settingType: MultipleCheckListType): Boolean {
        return when (settingType) {
            MultipleCheckListType.FUEL_CONNECTOR_TYPES -> {
                val selectedConnectorType =
                    FuelConnectorType.getConnectorByChargingType(settingsManager.chargingType.value)[index]
                val fuelConnectorTypes = settingsManager.fuelConnectorTypes.value.toMutableSet()
                val isChecked = fuelConnectorTypes.add(selectedConnectorType)
                    .takeIf { fuelConnectorTypes.size > 1 } ?: true
                if (!isChecked) {
                    fuelConnectorTypes.remove(selectedConnectorType)
                }
                settingsManager.fuelConnectorTypes.value = fuelConnectorTypes
                isChecked
            }

        }
    }
}

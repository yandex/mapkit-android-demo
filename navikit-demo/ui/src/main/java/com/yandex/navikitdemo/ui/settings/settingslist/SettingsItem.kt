package com.yandex.navikitdemo.ui.settings.settingslist

import com.yandex.navikitdemo.domain.SettingModel

sealed interface SettingsItem {

    data class SectionTitle(val title: String) : SettingsItem

    data class EditFloat(val title: String, val setting: SettingModel<Float>) : SettingsItem

    data class NextScreen(val screen: SettingsScreen) : SettingsItem

    data class CheckList(val settingType: CheckListType) : SettingsItem

    data class MultipleCheckList(val settingType: MultipleCheckListType) : SettingsItem

    data class Toggle(val title: String, val setting: SettingModel<Boolean>) : SettingsItem

    data class Details(val title: String) : SettingsItem

    object SpeedLimits : SettingsItem
}

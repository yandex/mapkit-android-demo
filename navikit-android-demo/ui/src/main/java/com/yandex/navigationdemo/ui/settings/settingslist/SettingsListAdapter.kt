package com.yandex.navigationdemo.ui.settings.settingslist

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.yandex.navigationdemo.ui.settings.settingslist.delegates.CheckListDelegate
import com.yandex.navigationdemo.ui.settings.settingslist.delegates.DetailsDelegate
import com.yandex.navigationdemo.ui.settings.settingslist.delegates.EditFloatDelegate
import com.yandex.navigationdemo.ui.settings.settingslist.delegates.NextScreenDelegate
import com.yandex.navigationdemo.ui.settings.settingslist.delegates.SectionTitleDelegate
import com.yandex.navigationdemo.ui.settings.settingslist.delegates.SpeedLimitsDelegate
import com.yandex.navigationdemo.ui.settings.settingslist.delegates.ToggleDelegate
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
interface SettingsListAdapterFactory {
    fun create(
        openSettingsScreenCallback: (SettingsScreen) -> Unit
    ): SettingsListAdapter
}

class SettingsListAdapter @AssistedInject constructor (
    sectionTitleDelegate: SectionTitleDelegate,
    checkListDelegate: CheckListDelegate,
    editFloatDelegate: EditFloatDelegate,
    toggleDelegate: ToggleDelegate,
    detailsDelegate: DetailsDelegate,
    speedLimitsDelegate: SpeedLimitsDelegate,
    @Assisted openSettingsScreenCallback: (SettingsScreen) -> Unit,
) : ListDelegationAdapter<List<SettingsItem>>() {

    init {
        delegatesManager.apply {
            addDelegate(sectionTitleDelegate)
            addDelegate(checkListDelegate)
            addDelegate(NextScreenDelegate(openSettingsScreenCallback))
            addDelegate(editFloatDelegate)
            addDelegate(toggleDelegate)
            addDelegate(detailsDelegate)
            addDelegate(speedLimitsDelegate)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<SettingsItem>) {
        this.setItems(newItems)
        notifyDataSetChanged()
    }
}

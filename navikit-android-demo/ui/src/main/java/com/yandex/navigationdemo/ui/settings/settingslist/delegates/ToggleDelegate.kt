package com.yandex.navigationdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.yandex.navigationdemo.ui.R
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsItem
import javax.inject.Inject

class ToggleDelegate @Inject constructor() :
    BaseSettingsAdapterDelegate<SettingsItem.Toggle, SettingsToggleView>(SettingsItem.Toggle::class.java) {

    override fun onBind(
        item: SettingsItem.Toggle,
        holder: SingleViewHolder<SettingsToggleView>
    ) {
        holder.view.apply {
            titleText.text = item.title
            switchView.apply {
                setOnCheckedChangeListener(null)
                isChecked = item.setting.value
                setOnCheckedChangeListener { _, isChecked ->
                    item.setting.value = isChecked
                    switchView.isChecked = isChecked
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup) =
        SingleViewHolder<SettingsToggleView>(SettingsToggleView(parent.context))
}

class SettingsToggleView(context: Context) : FrameLayout(context) {
    val titleText: TextView by lazy { findViewById(R.id.text_title) }
    val switchView: SwitchMaterial by lazy { findViewById(R.id.view_switch) }

    init {
        inflate(context, R.layout.view_settings_toggle, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

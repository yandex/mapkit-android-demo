package com.yandex.navigationdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.yandex.navigationdemo.ui.R
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsItem
import javax.inject.Inject

class SectionTitleDelegate @Inject constructor():
    BaseSettingsAdapterDelegate<SettingsItem.SectionTitle, SettingsSectionTitleView>(SettingsItem.SectionTitle::class.java) {

    override fun onBind(
        item: SettingsItem.SectionTitle,
        holder: SingleViewHolder<SettingsSectionTitleView>
    ) {
        holder.view.apply {
            titleText.text = item.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): SingleViewHolder<SettingsSectionTitleView> {
        return SingleViewHolder(SettingsSectionTitleView(parent.context))
    }
}

class SettingsSectionTitleView(context: Context) : FrameLayout(context) {
    val titleText: TextView by lazy { findViewById(R.id.text_section_title) }

    init {
        inflate(context, R.layout.view_settings_section_title, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

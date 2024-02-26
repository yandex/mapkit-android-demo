package com.yandex.navikitdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.yandex.navikitdemo.ui.R
import com.yandex.navikitdemo.ui.settings.settingslist.SettingsItem
import com.yandex.navikitdemo.ui.settings.settingslist.SettingsScreen
import com.yandex.navikitdemo.ui.settings.settingslist.toScreenName

class NextScreenDelegate(
    private val openSettingsScreenCallback: (SettingsScreen) -> Unit,
) : BaseSettingsAdapterDelegate<SettingsItem.NextScreen, SettingsNextScreenView>(SettingsItem.NextScreen::class.java) {

    override fun onBind(
        item: SettingsItem.NextScreen,
        holder: SingleViewHolder<SettingsNextScreenView>
    ) {
        holder.view.apply {
            titleText.text = item.screen.toScreenName(context)
            setOnClickListener { openSettingsScreenCallback(item.screen) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup) =
        SingleViewHolder<SettingsNextScreenView>(SettingsNextScreenView(parent.context))
}

class SettingsNextScreenView(context: Context) : FrameLayout(context) {
    val titleText: TextView by lazy { findViewById(R.id.text_title) }

    init {
        inflate(context, R.layout.view_settings_next_screen, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

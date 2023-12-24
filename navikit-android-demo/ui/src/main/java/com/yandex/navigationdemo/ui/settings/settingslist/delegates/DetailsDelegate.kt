package com.yandex.navigationdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.yandex.navigationdemo.ui.R
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsItem
import javax.inject.Inject

class DetailsDelegate @Inject constructor() :
    BaseSettingsAdapterDelegate<SettingsItem.Details, SettingsDetailsView>(SettingsItem.Details::class.java) {

    override fun onBind(
        item: SettingsItem.Details,
        holder: SingleViewHolder<SettingsDetailsView>
    ) {
        holder.view.apply {
            titleText.text = item.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): SingleViewHolder<SettingsDetailsView> {
        return SingleViewHolder(SettingsDetailsView(parent.context))
    }
}

class SettingsDetailsView(context: Context) : FrameLayout(context) {
    val titleText: TextView by lazy { findViewById(R.id.text_title) }

    init {
        inflate(context, R.layout.view_settings_details, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

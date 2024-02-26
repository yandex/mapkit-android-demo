package com.yandex.navikitdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.yandex.navikitdemo.ui.R
import com.yandex.navikitdemo.ui.settings.settingslist.SettingsItem
import javax.inject.Inject

class EditFloatDelegate @Inject constructor() :
    BaseSettingsAdapterDelegate<SettingsItem.EditFloat, SettingsEditTextFloatView>(SettingsItem.EditFloat::class.java) {

    override fun onBind(
        item: SettingsItem.EditFloat,
        holder: SingleViewHolder<SettingsEditTextFloatView>
    ) {
        holder.view.apply {
            titleText.text = item.title
            parameterEdit.apply {
                text = item.setting.value.toString()
                doOnTextChanged { text, start, before, count ->
                    text.toString().toFloatOrNull()?.let {
                        item.setting.value = it
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup) =
        SingleViewHolder<SettingsEditTextFloatView>(SettingsEditTextFloatView(parent.context))
}

class SettingsEditTextFloatView(context: Context) : FrameLayout(context) {
    val titleText: TextView by lazy { findViewById(R.id.text_title) }
    val parameterEdit: TextView by lazy { findViewById(R.id.edit_parameter) }

    init {
        inflate(context, R.layout.view_settings_edit_float, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

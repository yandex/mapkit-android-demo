package com.yandex.navigationdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.yandex.navigationdemo.ui.R
import com.yandex.navigationdemo.ui.settings.settingslist.CheckListInteractor
import com.yandex.navigationdemo.ui.settings.settingslist.CheckListType
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsItem
import javax.inject.Inject

class CheckListDelegate @Inject constructor(
    private val interactor: CheckListInteractor,
) : BaseSettingsAdapterDelegate<SettingsItem.CheckList, SettingsCheckListView>(SettingsItem.CheckList::class.java) {

    override fun onBind(
        item: SettingsItem.CheckList,
        holder: SingleViewHolder<SettingsCheckListView>
    ) {
        holder.view.let { view ->
            updateUi(view, item.settingType)
            view.setOnClickListener {
                PopupMenu(view.context, view).apply {
                    interactor.viewState(item.settingType).options.forEachIndexed { index, option ->
                        menu.add(option).setOnMenuItemClickListener {
                            interactor.onMenuItemClicked(index, item.settingType)
                            true
                        }
                    }
                    setOnDismissListener {
                        updateUi(view, item.settingType)
                    }
                    show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): SingleViewHolder<SettingsCheckListView> {
        return SingleViewHolder(SettingsCheckListView(parent.context))
    }

    private fun updateUi(view: SettingsCheckListView, type: CheckListType) {
        val viewState = interactor.viewState(type)
        view.apply {
            titleText.text = viewState.title
            valueText.text = viewState.selected
        }
    }
}

class SettingsCheckListView(context: Context) : FrameLayout(context) {
    val titleText: TextView by lazy { findViewById(R.id.text_check_list_title) }
    val valueText: TextView by lazy { findViewById(R.id.text_check_list_value) }

    init {
        inflate(context, R.layout.view_settings_check_list, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

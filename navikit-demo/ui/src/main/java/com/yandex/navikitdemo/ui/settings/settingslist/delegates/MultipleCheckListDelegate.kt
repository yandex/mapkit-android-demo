package com.yandex.navikitdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.yandex.navikitdemo.ui.R
import com.yandex.navikitdemo.ui.settings.settingslist.MultipleCheckListInteractor
import com.yandex.navikitdemo.ui.settings.settingslist.MultipleCheckListType
import com.yandex.navikitdemo.ui.settings.settingslist.SettingsItem
import javax.inject.Inject

class MultipleCheckListDelegate @Inject constructor(
    private val interactor: MultipleCheckListInteractor,
) : BaseSettingsAdapterDelegate<SettingsItem.MultipleCheckList, SettingsMultipleCheckListView>(
    SettingsItem.MultipleCheckList::class.java
) {

    private val onActionExpandListener = object : MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem): Boolean = false
        override fun onMenuItemActionCollapse(item: MenuItem): Boolean = false
    }

    override fun onBind(
        item: SettingsItem.MultipleCheckList,
        holder: SingleViewHolder<SettingsMultipleCheckListView>
    ) {
        holder.view.let { view ->
            updateUi(view, item.settingType)
            view.setOnClickListener {
                PopupMenu(view.context, view).apply {
                    val viewState = interactor.viewState(item.settingType)
                    viewState.options.forEachIndexed { index, option ->
                        menu.add(option)
                            .setChecked(viewState.selected.contains(option))
                            .setOnMenuItemClickListener {
                                val isChecked =
                                    interactor.onMenuItemClicked(index, item.settingType)
                                it.setChecked(isChecked)
                                    .setActionView(view)
                                    .setOnActionExpandListener(onActionExpandListener)
                                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                                updateUi(view, item.settingType)
                                false
                            }
                    }
                    menu.setGroupCheckable(0, true, false)
                    setOnDismissListener {
                        updateUi(view, item.settingType)
                    }
                    show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): SingleViewHolder<SettingsMultipleCheckListView> {
        return SingleViewHolder(SettingsMultipleCheckListView(parent.context))
    }

    private fun updateUi(view: SettingsMultipleCheckListView, type: MultipleCheckListType) {
        val viewState = interactor.viewState(type)
        val countText = view.resources.getString(
            R.string.settings_multiple_checklist_count,
            viewState.selected.count() - 1
        ).takeIf { viewState.selected.size > 1 }

        view.apply {
            titleText.text = viewState.title
            valueText.text = viewState.selected.first()
            additionalText.text = countText ?: ""
        }
    }

}

class SettingsMultipleCheckListView(context: Context) : FrameLayout(context) {

    val titleText: TextView by lazy { findViewById(R.id.text_multiple_check_list_title) }
    val valueText: TextView by lazy { findViewById(R.id.text_multiple_check_list_value) }
    val additionalText: TextView by lazy { findViewById(R.id.text_multiple_check_list_additional_value) }

    init {
        inflate(context, R.layout.view_settings_multiple_check_list, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

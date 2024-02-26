package com.yandex.navikitdemo.ui.settings.settingslist.delegates

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.yandex.navikitdemo.ui.settings.settingslist.SettingsItem

class SingleViewHolder<T : View>(view: View) : RecyclerView.ViewHolder(view) {
    @Suppress("UNCHECKED_CAST")
    val view: T = view as T
}

abstract class BaseSettingsAdapterDelegate<T : SettingsItem, V : View>(
    private val itemClass: Class<T>,
) : AbsListItemAdapterDelegate<T, SettingsItem, SingleViewHolder<V>>() {

    override fun isForViewType(
        item: SettingsItem,
        items: MutableList<SettingsItem>,
        position: Int
    ): Boolean {
        return itemClass.isInstance(item)
    }

    override fun onBindViewHolder(
        item: T,
        holder: SingleViewHolder<V>,
        payloads: MutableList<Any>
    ) {
        onBind(item, holder)
    }

    abstract fun onBind(item: T, holder: SingleViewHolder<V>)
}

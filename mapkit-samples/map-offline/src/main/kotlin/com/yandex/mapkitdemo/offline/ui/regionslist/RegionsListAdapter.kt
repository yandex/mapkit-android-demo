package com.yandex.mapkitdemo.offline.ui.regionslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkitdemo.common.goneOrRun
import com.yandex.mapkitdemo.offline.databinding.ItemRegionsListBinding

sealed interface RegionsListItem {
    data class RegionItem(
        val id: Int,
        val name: String,
        val cities: List<String>
    ) : RegionsListItem

    data class SectionItem(val title: String) : RegionsListItem
}

class RegionsListAdapter : RecyclerView.Adapter<RegionItemHolder>() {
    var items: List<RegionsListItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onRegionItemClickListener: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionItemHolder {
        return RegionItemHolder(
            ItemRegionsListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onRegionItemClickListener,
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RegionItemHolder, position: Int) = holder.bind(items[position])
}

class RegionItemHolder(
    private val binding: ItemRegionsListBinding,
    private val onRegionItemClickListener: (Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: RegionsListItem) {
        val regionItem = item as? RegionsListItem.RegionItem
        val sectionItem = item as? RegionsListItem.SectionItem

        binding.apply {
            textName.goneOrRun(regionItem) {
                text = it.name
            }
            textCities.goneOrRun(regionItem) {
                text = it.cities.joinToString(", ")
                isVisible = text.isNotEmpty()
            }
            textSectionTitle.goneOrRun(sectionItem) {
                text = it.title
            }

            root.apply {
                isClickable = regionItem != null
                if (regionItem != null) {
                    setOnClickListener { onRegionItemClickListener(regionItem.id) }
                }
            }
        }
    }
}

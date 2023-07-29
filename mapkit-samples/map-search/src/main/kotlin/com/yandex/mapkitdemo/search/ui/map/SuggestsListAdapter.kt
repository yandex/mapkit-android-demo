package com.yandex.mapkitdemo.search.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.SpannableString
import com.yandex.mapkitdemo.common.CommonColors
import com.yandex.mapkitdemo.search.data.toSpannable
import com.yandex.mapkitdemo.search.databinding.ItemSuggestBinding

class SuggestsListAdapter : RecyclerView.Adapter<SuggestHolder>() {
    var items: List<SuggestHolderItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestHolder {
        return SuggestHolder(
            ItemSuggestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SuggestHolder, position: Int) =
        holder.bind(items[position])
}

class SuggestHolder(
    private val binding: ItemSuggestBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val spanColor = ContextCompat.getColor(binding.root.context, CommonColors.black)

    fun bind(item: SuggestHolderItem) = with(binding) {
        textTitle.text = item.title.toSpannable(spanColor)
        textSubtitle.text = item.subtitle?.toSpannable(spanColor)
        binding.root.setOnClickListener { item.onClick() }
    }
}

data class SuggestHolderItem(
    val title: SpannableString,
    val subtitle: SpannableString?,
    val onClick: () -> Unit,
)

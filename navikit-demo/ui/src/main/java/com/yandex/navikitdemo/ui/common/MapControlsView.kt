package com.yandex.navikitdemo.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.yandex.navikitdemo.ui.R
import com.yandex.navikitdemo.ui.databinding.ViewMapControlsBinding

class MapControlsView(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    private val binding: ViewMapControlsBinding

    init {
        inflate(context, R.layout.view_map_controls, this)
        binding = ViewMapControlsBinding.bind(this)
    }

    fun setSettingsButtonClickCallback(callback: () -> Unit) {
        binding.buttonSettings.setOnClickListener { callback() }
    }

    fun setPlusButtonClickCallback(callback: () -> Unit) {
        binding.buttonZoomPlus.setOnClickListener { callback() }
    }

    fun setMinusButtonClickCallback(callback: () -> Unit) {
        binding.buttonZoomMinus.setOnClickListener { callback() }
    }

    fun setFindMeButtonClickCallback(callback: () -> Unit) {
        binding.buttonFindMe.setOnClickListener { callback() }
    }

    fun setOverviewButtonClickCallback(callback: () -> Unit) {
        binding.buttonOverview.setOnClickListener { callback() }
    }

    fun setOverviewButtonVisibility(visibility: Boolean) {
        binding.buttonOverview.isVisible = visibility
    }
}

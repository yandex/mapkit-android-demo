package com.yandex.navikitdemo.ui.guidance.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.yandex.navikitdemo.ui.R
import com.yandex.navikitdemo.ui.databinding.ViewSimultaionPanelBinding

class SimulationPanelView(
    context: Context,
    attributeSet: AttributeSet,
) : FrameLayout(context, attributeSet) {

    private val binding: ViewSimultaionPanelBinding

    init {
        inflate(context, R.layout.view_simultaion_panel, this)
        binding = ViewSimultaionPanelBinding.bind(this)
    }

    fun setPlusClickCallback(callback: () -> Unit) {
        binding.buttonSpeedPlus.setOnClickListener { callback() }
    }

    fun setMinusClickCallback(callback: () -> Unit) {
        binding.buttonSpeedMinus.setOnClickListener { callback() }
    }

    fun setSpeedText(text: String) {
        binding.textSimulationSpeed.text = text
    }
}

package com.yandex.navikitdemo.ui.guidance.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.yandex.navikitdemo.domain.utils.localizeSpeed
import com.yandex.navikitdemo.ui.R
import com.yandex.navikitdemo.ui.databinding.ViewSpeedLimitBinding

data class SpeedLimitViewState(
    val currentSpeed: Double,
    val limitSpeed: Double,
    val speedLimitExceeded: Boolean,
)

class SpeedLimitView(
    context: Context,
    attributeSet: AttributeSet,
) : FrameLayout(context, attributeSet) {

    private val binding: ViewSpeedLimitBinding

    init {
        inflate(context, R.layout.view_speed_limit, this)
        binding = ViewSpeedLimitBinding.bind(this)
    }

    fun render(state: SpeedLimitViewState) {
        binding.textCurrentSpeed.apply {
            text = state.currentSpeed.localizeSpeed()
            val textColor = if (state.speedLimitExceeded) R.color.alert else R.color.text_primary
            setTextColor(ContextCompat.getColor(context, textColor))
        }
        binding.textLimitSpeed.text = state.limitSpeed.localizeSpeed()
    }
}

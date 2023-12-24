package com.yandex.navigationdemo.ui.settings.settingslist.delegates

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import com.yandex.navigationdemo.ui.R
import com.yandex.navigationdemo.ui.databinding.ViewSettingsSpeedLimitsBinding
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsItem
import com.yandex.navigationdemo.ui.settings.settingslist.SpeedLimitsInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SpeedLimitsDelegate @Inject constructor(
    private val scope: CoroutineScope,
    private val speedLimitsInteractor: SpeedLimitsInteractor,
) :
    BaseSettingsAdapterDelegate<SettingsItem.SpeedLimits, SpeedLimitsView>(SettingsItem.SpeedLimits::class.java) {

    override fun onBind(
        item: SettingsItem.SpeedLimits,
        holder: SingleViewHolder<SpeedLimitsView>
    ) {
        speedLimitsInteractor.viewStateChanges
            .onEach { updateUi(holder.view.binding) }
            .launchIn(scope)
    }

    override fun onCreateViewHolder(parent: ViewGroup): SingleViewHolder<SpeedLimitsView> {
        return SingleViewHolder(SpeedLimitsView(parent.context))
    }

    private fun updateUi(binding: ViewSettingsSpeedLimitsBinding) {
        val policy = speedLimitsInteractor.speedLimitsPolicy
        val legal = policy.legalSpeedLimits
        val custom = policy.customSpeedLimits(speedLimitsInteractor.speedLimitsTolerance)
        val prefix = if (speedLimitsInteractor.speedLimitsTolerance > 1.0) " (!)" else ""
        binding.apply {
            textLimitsUrban.text = "Urban: ${legal.urban.text} - ${custom.urban.text} $prefix"
            textLimitsRural.text = "Rural: ${legal.rural.text} - ${custom.rural.text} $prefix"
            textLimitsExpressway.text = "Expressway: ${legal.expressway.text} - ${custom.expressway.text} $prefix"
        }
    }
}

class SpeedLimitsView(context: Context) : LinearLayout(context) {
    val binding: ViewSettingsSpeedLimitsBinding

    init {
        inflate(context, R.layout.view_settings_speed_limits, this)
        binding = ViewSettingsSpeedLimitsBinding.bind(this)
        orientation = VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
}

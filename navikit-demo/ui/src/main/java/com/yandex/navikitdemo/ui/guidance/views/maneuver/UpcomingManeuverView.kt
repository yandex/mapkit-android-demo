package com.yandex.navikitdemo.ui.guidance.views.maneuver

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.yandex.mapkit.directions.driving.Action
import com.yandex.mapkit.directions.driving.LaneSign
import com.yandex.navikitdemo.ui.databinding.ViewUpcomingManeuverBinding
import com.yandex.navikitdemo.ui.guidance.views.maneuver.lanes.LaneItemsFactory
import com.yandex.navikitdemo.ui.guidance.views.maneuver.lanes.LaneSignContainerBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

data class UpcomingManeuverViewState(
    val distance: String,
    val action: Action?,
    val nextStreet: String? = null,
    val laneSign: LaneSign? = null,
)

@AndroidEntryPoint
class UpcomingManeuverView constructor(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {

    @Inject
    lateinit var resourcesProvider: UpcomingManeuverResourcesProvider

    private val laneItemsFactory by lazy { LaneItemsFactory(resourcesProvider) }

    private val binding: ViewUpcomingManeuverBinding

    init {
        binding = ViewUpcomingManeuverBinding.inflate(LayoutInflater.from(context), this)
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    fun render(viewState: UpcomingManeuverViewState) = binding.apply {
        textDistance.text = viewState.distance

        viewState.nextStreet.let {
            textNextStreet.apply {
                text = it
                isVisible = it != null
            }
        }

        viewState.action?.let { action ->
            resourcesProvider.getManeuverResource(action)?.also {
                imageAction.setImageResource(it)
            }.also {
                imageAction.isVisible = it != null
            }
        }

        viewState.laneSign?.also {
            updateLaneSignUi(it)
        }.also {
            layoutLanesSign.isVisible = it != null
        }
    }

    private fun updateLaneSignUi(laneSign: LaneSign) {
        val laneItems = laneItemsFactory.createLaneItems(laneSign, context)
        LaneSignContainerBuilder(context, laneItems, binding.layoutLanesSign, resourcesProvider).build()
    }
}

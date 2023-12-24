package com.yandex.navigationdemo.ui.guidance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.navigation.guidance_camera.CameraMode
import com.yandex.navigationdemo.domain.NavigationLayerManager
import com.yandex.navigationdemo.ui.R
import com.yandex.navigationdemo.ui.common.BaseMapFragment
import com.yandex.navigationdemo.ui.databinding.FragmentGuidanceBinding
import com.yandex.navigationdemo.ui.guidance.views.SpeedLimitViewState
import com.yandex.navigationdemo.ui.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class GuidanceUiState(
    val roadNameText: String,
    val roadFlagsText: String,
    val timeLeftText: String,
    val distanceLeftText: String,
    val speedLimitViewState: SpeedLimitViewState?,
    val simulationSpeed: String,
    val simulationPanelVisible: Boolean,
)

@AndroidEntryPoint
class GuidanceFragment : BaseMapFragment(R.layout.fragment_guidance) {

    private lateinit var binding: FragmentGuidanceBinding
    private val viewModel: GuidanceViewModel by viewModels()

    @Inject
    lateinit var navigationLayerManager: NavigationLayerManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuidanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            showCloseGuidanceDialog()
        }

        binding.apply {
            viewMapControls.apply {
                setFindMeButtonClickCallback {
                    navigationLayerManager.cameraMode = CameraMode.FOLLOWING
                }
                setOverviewButtonClickCallback {
                    navigationLayerManager.cameraMode = CameraMode.OVERVIEW
                }
                setOverviewButtonVisibility(true)
            }

            buttonCloseGuidance.setOnClickListener {
                showCloseGuidanceDialog()
            }

            viewSimulationPanel.apply {
                setPlusClickCallback { viewModel.changeSimulationSpeed(GuidanceViewModel.SimulationSpeedChange.INCREASE) }
                setMinusClickCallback { viewModel.changeSimulationSpeed(GuidanceViewModel.SimulationSpeedChange.DECREES) }
            }
        }

        viewModel.apply {
            guidanceUiState().subscribe(viewLifecycleOwner) {
                binding.apply {
                    textRoadName.text = it.roadNameText
                    textRouteFlags.text = it.roadFlagsText
                    textTimeLeft.text = it.timeLeftText
                    textDistanceLeft.text = it.distanceLeftText
                    viewSpeedLimit.isVisible = it.speedLimitViewState != null
                    it.speedLimitViewState?.let { state -> viewSpeedLimit.render(state) }
                    viewSimulationPanel.setSpeedText(it.simulationSpeed)
                    viewSimulationPanel.isVisible = it.simulationPanelVisible
                }
            }

            combine(
                upcomingManeuverViewState(),
                navigationLayerManager.maneuverBalloonVisibility,
                navigationLayerManager.cameraFollowingMode,
            ) { maneuverViewStateOrNull, isManeuverBalloonVisible, isCameraFollowing ->
                val maneuverViewState = maneuverViewStateOrNull ?: run {
                    binding.viewUpcomingManeuver.isVisible = false
                    return@combine
                }
                binding.viewUpcomingManeuver.isVisible =
                    !isCameraFollowing || !isManeuverBalloonVisible
                binding.viewUpcomingManeuver.render(maneuverViewState)
            }.subscribe(viewLifecycleOwner)

            guidanceFinished.subscribe(viewLifecycleOwner) {
                closeGuidance()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startGuidanceIfNeeded(navigationLayerManager.selectedRoute)
        viewModel.startBackgroundGuidanceServiceIfNeeded()
        navigationLayerManager.cameraMode = CameraMode.FOLLOWING
    }

    override fun calculateFocusBounds(): FocusBounds {
        return super.calculateFocusBounds().run {
            val placemarkPadding = resources.getDimension(R.dimen.placemark_in_guidance_padding)
            val newBottom = bottom - binding.layoutCardContent.height
            val newTop = top + resources.getDimension(R.dimen.guidance_top_padding)
            copy(
                top = newTop,
                bottom = newBottom,
                point = point.first to newBottom - placemarkPadding,
            )
        }
    }

    private fun showCloseGuidanceDialog() {
        alertDialogFactory.closeGuidanceDialog(::closeGuidance).show()
    }

    private fun closeGuidance() {
        viewModel.stopGuidance()
        findNavController().popBackStack()
    }
}

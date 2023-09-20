package com.yandex.mapkitdemo.offline.ui.region

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yandex.mapkit.offline_cache.RegionState
import com.yandex.mapkitdemo.common.showToast
import com.yandex.mapkitdemo.offline.databinding.FragmentRegionBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

data class RegionUiState(
    val id: Int,
    val name: String,
    val country: String,
    val cities: List<String>,
    val center: Pair<Double, Double>,
    val size: String,
    val downloadProgress: Float,
    val parentId: Int?,
    val state: RegionState,
    val releaseTime: String?,
    val downloadedReleaseTime: String?,
)

class RegionFragment : Fragment() {
    private lateinit var binding: FragmentRegionBinding
    private val args: RegionFragmentArgs by navArgs()
    private val viewModel: RegionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegionBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = "Region Info"

        binding.apply {
            buttonStart.setOnClickListener {
                if (!viewModel.startDownload(args.regionId)) {
                    requireActivity().showToast("Not enough available space on device to download region with ${args.regionId} id")
                }
            }
            buttonStop.setOnClickListener { viewModel.stopDownload(args.regionId) }
            buttonPause.setOnClickListener { viewModel.pauseDownload(args.regionId) }
            buttonDrop.setOnClickListener { viewModel.drop(args.regionId) }
        }

        viewModel.uiState(regionId = args.regionId)
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.apply {
                    textError.isVisible = it == null
                    layoutRegionContent.isVisible = it != null
                    if (it == null) return@onEach

                    textRegionId.text = "Id: ${it.id}"
                    textName.text = "Name: ${it.name}"
                    textCountry.text = "Country: ${it.country}"
                    textCities.text = "Cities: ${it.cities}"
                    textCenter.text = "Center: (${it.center.first}, ${it.center.second})"
                    textSize.text = "Size: ${it.size}"
                    textParentId.text = "Parent id: ${it.parentId}"
                    textState.text = "State: ${it.state}"
                    textReleaseTime.text = "Release time: ${it.releaseTime}"
                    textDownloadedReleaseTime.text = "Downloaded time: ${it.downloadedReleaseTime}"

                    viewProgressBar.apply {
                        isVisible = it.state in listOf(RegionState.DOWNLOADING, RegionState.PAUSED)
                        max = 100
                        progress = (max * it.downloadProgress).roundToInt()
                    }

                    buttonShowOnMap.setOnClickListener { _ -> navigateToLocation(it.center) }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToLocation(point: Pair<Double, Double>) {
        val action = RegionFragmentDirections.actionRegionFragmentToMapFragment(
            point.first.toFloat(),
            point.second.toFloat(),
        )
        findNavController().navigate(action)
    }
}

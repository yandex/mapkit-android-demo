package com.yandex.mapkitdemo.offline.ui.options

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.yandex.mapkitdemo.common.showToast
import com.yandex.mapkitdemo.offline.databinding.FragmentOptionsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class OptionsUiState(
    val allowCellularNetwork: Boolean = false,
    val autoUpdateEnabled: Boolean = false,
)

class OptionsFragment : Fragment() {
    private lateinit var binding: FragmentOptionsBinding
    private val viewModel: OptionsViewModel by viewModels { OptionsViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentOptionsBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "Options"

        binding.apply {
            buttonCacheSize.setOnClickListener {
                viewModel.calculateCacheSize()
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .onEach {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Total cache size: $it bytes")
                            .setPositiveButton("ok", null)
                            .show()
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
            buttonClearCache.setOnClickListener {
                viewModel.clearCache()
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .onEach {
                        AlertDialog.Builder(requireContext())
                            .setTitle("All caches were cleared")
                            .setPositiveButton("ok", null)
                            .show()
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }

            buttonMove.setOnClickListener {
                viewModel.movePath(editCachesPath.text.toString())
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .onEach {
                        when (it) {
                            is OptionsViewModel.MovePathState.Progress -> {
                                viewProgressBar.apply {
                                    isVisible = true
                                    progress = it.value
                                }
                            }
                            is OptionsViewModel.MovePathState.Completed -> {
                                viewProgressBar.isVisible = false
                                requireActivity().showToast("Caches moved to: ${editCachesPath.text}")
                            }
                            is OptionsViewModel.MovePathState.Error -> {
                                viewProgressBar.isVisible = false
                                requireActivity().showToast("Error on moving: ${it.error}")
                            }
                        }
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }

            buttonSwitch.setOnClickListener {
                viewModel.switchPath(editCachesPath.text.toString())
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .onEach {
                        when (it) {
                            is OptionsViewModel.PathSetState.Error ->
                                requireActivity().showToast("Error on setting path: ${it.error}")
                            OptionsViewModel.PathSetState.Success ->
                                requireActivity().showToast("Success path set: ${editCachesPath.text}")
                        }
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }

            checkCellularNetwork.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setCellularNetwork(isChecked)
            }
            checkAutoUpdate.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setAutoUpdateEnabled(isChecked)
            }
        }

        viewModel.requestCachesPath()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.editCachesPath.setText(it)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.uiState()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.apply {
                    checkCellularNetwork.isChecked = it.allowCellularNetwork
                    checkAutoUpdate.isChecked = it.autoUpdateEnabled
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}

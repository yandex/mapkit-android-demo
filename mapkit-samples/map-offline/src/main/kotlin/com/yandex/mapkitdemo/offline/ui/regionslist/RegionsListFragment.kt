package com.yandex.mapkitdemo.offline.ui.regionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkitdemo.offline.databinding.FragmentRegionsListBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class RegionsListUiState(
    val regionsListItems: List<RegionsListItem>,
)

class RegionsListFragment : Fragment() {
    private lateinit var binding: FragmentRegionsListBinding
    private val viewModel: RegionsListViewModel by viewModels()

    private val regionsListAdapter = RegionsListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegionsListBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = "Regions List"

        binding.apply {
            recyclerRegionsList.apply {
                regionsListAdapter.onRegionItemClickListener = {
                    navigateToRegionFragment(it)
                }
                adapter = regionsListAdapter
                addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            }

            editSearchRegions.doOnTextChanged { text, _, _, _ ->
                viewModel.searchRegions(text.toString())
            }
        }

        viewModel.uiState()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { regionsListAdapter.items = it.regionsListItems }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToRegionFragment(regionId: Int) {
        val action =
            RegionsListFragmentDirections.actionRegionsListFragmentToRegionFragment(regionId)
        findNavController().navigate(action)
    }
}

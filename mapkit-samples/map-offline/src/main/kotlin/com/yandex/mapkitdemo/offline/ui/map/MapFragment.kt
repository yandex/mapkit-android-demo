package com.yandex.mapkitdemo.offline.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkitdemo.offline.R
import com.yandex.mapkitdemo.offline.databinding.FragmentMapBinding
import com.yandex.mapkitdemo.offline.ui.Activity

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private val args: MapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMapBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = requireContext().getString(R.string.app_name)

        val isMoved = args.run {
            val latitude = latitude.takeIf { it >= 0 } ?: return@run false
            val longitude = longitude.takeIf { it >= 0 } ?: return@run false
            val point = Point(latitude.toDouble(), longitude.toDouble())
            (activity as Activity).mapMove(point)
            true
        }

        binding.apply {
            buttonRegionsList.isVisible = !isMoved
            buttonRegionsList.setOnClickListener {
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToRegionsListFragment())
            }
            buttonOptions.isVisible = !isMoved
            buttonOptions.setOnClickListener {
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToOptionsFragment())
            }
        }
    }
}

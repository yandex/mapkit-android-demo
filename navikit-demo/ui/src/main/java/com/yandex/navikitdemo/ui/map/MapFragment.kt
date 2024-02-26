package com.yandex.navikitdemo.ui.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.geometry.Point
import com.yandex.navikitdemo.ui.R
import com.yandex.navikitdemo.ui.common.BaseMapFragment
import com.yandex.navikitdemo.ui.utils.subscribe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : BaseMapFragment(R.layout.fragment_map) {

    private val viewModel: MapViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null && viewModel.isGuidanceInProgress()) {
            openGuidance()
        }

        mapControlsView.setFindMeButtonClickCallback {
            cameraManager.moveCameraToUserLocation()
        }

        mapTapManager.longTapActions.subscribe(viewLifecycleOwner) {
            showDialogAlert(it)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.clearNavigationSerialization()
    }

    private fun showDialogAlert(point: Point) {
        alertDialogFactory
            .requestToPointDialog {
                viewModel.setToPoint(point)
                openRouteVariants()
            }
            .show()
    }

    private fun openRouteVariants() {
        val action = MapFragmentDirections.actionMapFragmentToRouteVariantsFragment()
        findNavController().navigate(action)
    }

    private fun openGuidance() {
        val action = MapFragmentDirections.actionMapFragmentToGuidanceFragment()
        findNavController().navigate(action)
    }
}

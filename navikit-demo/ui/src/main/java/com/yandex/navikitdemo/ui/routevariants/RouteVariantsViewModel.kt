package com.yandex.navikitdemo.ui.routevariants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.models.State
import com.yandex.navikitdemo.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class RouteVariantsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val requestPointsManager: RequestPointsManager,
    private val locationManager: LocationManager,
) : ViewModel() {

    init {
        subscribeForRequestRoutes().launchIn(viewModelScope)
        subscribeForFirstLocationObtained().launchIn(viewModelScope)
    }

    fun routeVariantsUiState(): Flow<RouteVariantsUiState> {
        return combine(
            requestPointsManager.requestPoints,
            navigationManager.navigationRouteState
        ) { requestPoints, navigationRouteState ->
            val hasRequestPoints = requestPoints.isEmpty()
            val message =
                if (navigationRouteState is State.Error) R.string.route_variants_error
                else null
            RouteVariantsUiState(
                hasRequestPoints = hasRequestPoints,
                errorMessage = message
            )
        }.distinctUntilChanged()
    }

    fun setFromPoint(point: Point) = requestPointsManager.setFromPoint(point)
    fun setToPoint(point: Point) = requestPointsManager.setToPoint(point)
    fun addViaPoint(point: Point) = requestPointsManager.addViaPoint(point)
    fun resetRouteVariants() = requestPointsManager.resetPoints()

    private fun subscribeForRequestRoutes(): Flow<*> {
        return requestPointsManager.requestPoints
            .onEach {
                if (it.isNotEmpty()) {
                    navigationManager.requestRoutes(it)
                } else {
                    navigationManager.resetRoutes()
                }
            }
    }

    private fun subscribeForFirstLocationObtained(): Flow<*> {
        return locationManager.location()
            .filterNotNull()
            .take(1)
            .onEach {
                setFromPoint(it.position)
            }
    }

}

package com.yandex.navikitdemo.ui.routevariants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.models.NavigationState
import com.yandex.navikitdemo.domain.smartroute.SmartRoutePlanningFactory
import com.yandex.navikitdemo.domain.utils.smartRouteOptionsChanges
import com.yandex.navikitdemo.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RouteVariantsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val smartRoutePlanningFactory: SmartRoutePlanningFactory,
    private val requestPointsManager: RequestPointsManager,
    private val locationManager: LocationManager,
    private val settingsManager: SettingsManager,
    private val vehicleOptionsManager: VehicleOptionsManager,
) : ViewModel() {

    private val uiStateImpl = MutableStateFlow(RouteVariantsUiState())
    val uiState: StateFlow<RouteVariantsUiState> = uiStateImpl.asStateFlow()

    init {
        subscribeForRequestRoutes().launchIn(viewModelScope)
        subscribeForFirstLocationObtained().launchIn(viewModelScope)
        subscribeForRouteVariants().launchIn(viewModelScope)
    }

    fun setFromPoint(point: Point) = requestPointsManager.setFromPoint(point)
    fun setToPoint(point: Point) = requestPointsManager.setToPoint(point)
    fun addViaPoint(point: Point) = requestPointsManager.addViaPoint(point)
    fun resetRouteVariants() = requestPointsManager.resetPoints()
    fun errorMessageShown() {
        uiStateImpl.update { it.copy(errorMessage = null) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeForRequestRoutes(): Flow<*> {
        return requestPointsManager.requestPoints.flatMapLatest { points ->
            val isSmartRouteEnabled =
                settingsManager.smartRoutePlanningEnabled.value.takeIf { points.isNotEmpty() }
            if (isSmartRouteEnabled == true) {
                requestSmartRoutes(points)
            } else {
                flowOf(points)
            }
        }
            .onEach { requestRoutes(it) }
    }

    private fun subscribeForFirstLocationObtained(): Flow<*> {
        return locationManager.location()
            .filterNotNull()
            .take(1)
            .onEach { setFromPoint(it.position) }
    }

    private fun subscribeForRouteVariants(): Flow<*> {
        return combine(
            requestPointsManager.requestPoints,
            navigationManager.navigationRouteState,
        ) { requestPoints, navigationRouteState ->
            val hasRequestPoints = requestPoints.isEmpty()
            val message = if (navigationRouteState is NavigationState.Error) {
                R.string.route_variants_error
            } else {
                null
            }
            uiStateImpl.update {
                it.copy(
                    hasRequestPoints = hasRequestPoints,
                    errorMessage = message
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun requestSmartRoutes(points: List<RequestPoint>): Flow<List<RequestPoint>?> {
        return if (!points.isSmartRouteSupported()) {
            showErrorMessage(R.string.smart_route_with_via_points_error)
            flowOf(points)
        } else {
            settingsManager.smartRouteOptionsChanges().filterNotNull().mapLatest {
                val smartRouteResult = smartRoutePlanningFactory.requestRoutes(
                    points.first(),
                    points.last(),
                    vehicleOptionsManager.vehicleOptions(),
                    it
                )
                if (smartRouteResult.isFailure) {
                    showErrorMessage(R.string.route_variants_error)
                }
                smartRouteResult.getOrNull()
            }
        }
    }

    private fun requestRoutes(points: List<RequestPoint>?) {
        if (!points.isNullOrEmpty()) {
            navigationManager.requestRoutes(points)
        } else {
            navigationManager.resetRoutes()
        }
    }

    private fun showErrorMessage(errorMessage: Int?) {
        uiStateImpl.update { it.copy(errorMessage = errorMessage) }
    }

    private fun List<RequestPoint>.isSmartRouteSupported() = size == 2

}

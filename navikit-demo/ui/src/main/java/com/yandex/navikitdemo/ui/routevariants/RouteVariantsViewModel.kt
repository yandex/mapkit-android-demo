package com.yandex.navikitdemo.ui.routevariants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.models.SmartRouteState
import com.yandex.navikitdemo.domain.smartRoute.SmartRoutePlanningManager
import com.yandex.navikitdemo.domain.smartRoute.SmartRoutePlanningSession
import com.yandex.navikitdemo.domain.models.State
import com.yandex.navikitdemo.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RouteVariantsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val smartRoutePlanningManager: SmartRoutePlanningManager,
    private val requestPointsManager: RequestPointsManager,
    private val locationManager: LocationManager,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private var smartRoutePlanningSession: SmartRoutePlanningSession? = null
    private var smartRoutePlanningJob: Job? = null

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

    private fun subscribeForRequestRoutes(): Flow<*> {
        return requestPointsManager.requestPoints
            .onEach {
                if (settingsManager.smartRoutePlanningEnabled.value)
                    requestSmartRoute(it)
                else
                    requestRoute(it)
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

    private fun subscribeForRouteVariants(): Flow<*> {
        return combine(
            requestPointsManager.requestPoints,
            navigationManager.navigationRouteState,
        ) { requestPoints, navigationRouteState ->
            val hasRequestPoints = requestPoints.isEmpty()
            val message = routeVariantsErrorMessage(navigationRouteState is State.Error)
            uiStateImpl.update {
                it.copy(
                    hasRequestPoints = hasRequestPoints,
                    errorMessage = message
                )
            }
        }
    }

    private fun requestSmartRoute(points: List<RequestPoint>) {
        smartRoutePlanningJob?.cancel()
        if (points.isNotEmpty()) {
            smartRoutePlanningManager.requestRoutes(points.first(), points.last()).also {
                smartRoutePlanningSession = it
                smartRoutePlanningJob = it.subscribeForSmartRoutePlanning()
                    .launchIn(viewModelScope)
            }
        } else {
            smartRoutePlanningSession?.reset()
        }
    }

    private fun requestRoute(points: List<RequestPoint>) {
        if (points.isNotEmpty()) {
            navigationManager.requestRoutes(points)
        } else {
            navigationManager.resetRoutes()
        }
    }

    private fun routeVariantsErrorMessage(isError: Boolean) =
        if (isError) R.string.route_variants_error else null

    private fun SmartRoutePlanningSession.subscribeForSmartRoutePlanning(): Flow<*> {
        return routeState
            .onEach {
                val message = routeVariantsErrorMessage(it is SmartRouteState.Error)
                uiStateImpl.update { it.copy(errorMessage = message) }
            }
    }

}

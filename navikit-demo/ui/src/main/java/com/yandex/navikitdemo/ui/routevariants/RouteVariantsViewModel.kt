package com.yandex.navikitdemo.ui.routevariants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@HiltViewModel
class RouteVariantsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val requestPointsManager: RequestPointsManager,
    private val locationManager: LocationManager,
) : ViewModel() {

    val hasRequestPoints: Flow<Boolean> = requestPointsManager.requestPoints
        .map { it.isEmpty() }.distinctUntilChanged()

    init {
        subscribeForRequestRoutes().launchIn(viewModelScope)
        subscribeForFirstLocationObtained().launchIn(viewModelScope)
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

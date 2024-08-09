package com.yandex.navikitdemo.data

import com.yandex.mapkit.LocalizedValue
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.navigation.automotive.Navigation
import com.yandex.mapkit.navigation.automotive.NavigationListener
import com.yandex.mapkit.navigation.automotive.RouteChangeReason
import com.yandex.mapkit.navigation.automotive.SpeedLimitStatus
import com.yandex.mapkit.navigation.automotive.SpeedLimitsPolicy
import com.yandex.mapkit.navigation.automotive.UpcomingLaneSign
import com.yandex.mapkit.navigation.automotive.UpcomingManoeuvre
import com.yandex.mapkit.navigation.automotive.WindshieldListener
import com.yandex.navikitdemo.domain.NavigationHolder
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SimulationManager
import com.yandex.navikitdemo.domain.VehicleOptionsManager
import com.yandex.navikitdemo.domain.helpers.BackgroundServiceManager
import com.yandex.navikitdemo.domain.helpers.SimpleGuidanceListener
import com.yandex.navikitdemo.domain.models.NavigationState
import com.yandex.navikitdemo.domain.utils.buildFlagsString
import com.yandex.runtime.Error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class NavigationManagerImpl @Inject constructor(
    private val routeRequestPointsManager: RequestPointsManager,
    private val vehicleOptionsManager: VehicleOptionsManager,
    private val settingsManager: SettingsManager,
    private val simulationManager: SimulationManager,
    private val backgroundServiceManager: BackgroundServiceManager,
    navigationHolder: NavigationHolder,
) : NavigationManager {

    private val mainScope = MainScope() + Dispatchers.Main.immediate
    private var navigation: Navigation = navigationHolder.navigation.value

    private val currentRouteImpl = MutableStateFlow<DrivingRoute?>(null)
    override val currentRoute: StateFlow<DrivingRoute?> = currentRouteImpl

    private val roadNameImpl = MutableStateFlow("")
    override val roadName: Flow<String> = roadNameImpl.buffer()

    override val roadFlags: Flow<String> = currentRoute.map { it?.buildFlagsString() ?: "" }

    private val upcomingManeuversImpl = MutableStateFlow(emptyList<UpcomingManoeuvre>())
    override val upcomingManeuvers: Flow<List<UpcomingManoeuvre>> = upcomingManeuversImpl

    private val upcomingLaneSignsImpl = MutableStateFlow(emptyList<UpcomingLaneSign>())
    override val upcomingLaneSigns: Flow<List<UpcomingLaneSign>> = upcomingLaneSignsImpl

    private val locationImpl = MutableStateFlow<Location?>(null)
    private var lastLocationTime: Long = 0

    override val speedLimit: LocalizedValue? = navigation.guidance.speedLimit
    override val speedLimitStatus: SpeedLimitStatus = navigation.guidance.speedLimitStatus
    override val speedLimitTolerance: Double = navigation.guidance.speedLimitTolerance
    override val speedLimitsPolicy: SpeedLimitsPolicy = navigation.guidance.speedLimitsPolicy

    private val navigationRouteStateImpl = MutableStateFlow<NavigationState>(NavigationState.Off)
    override val navigationRouteState = navigationRouteStateImpl

    private val guidanceListener = object : SimpleGuidanceListener() {
        override fun onLocationChanged() {
            if ((System.currentTimeMillis() - lastLocationTime).seconds < LOCATION_UPDATE_TIMEOUT) return
            lastLocationTime = System.currentTimeMillis()
            locationImpl.value = navigation.guidance.location
        }

        override fun onRouteFinished() {
            CoroutineScope(Dispatchers.IO).launch {
                // Stop guidance with delay, so the route finish annotation doesn't cancel.
                delay(0.1.seconds)
                withContext(Dispatchers.Main) {
                    stopGuidance()
                }
            }
        }

        override fun onRoadNameChanged() {
            roadNameImpl.value = navigation.guidance.roadName ?: ""
        }

        override fun onCurrentRouteChanged(reason: RouteChangeReason) {
            currentRouteImpl.value = navigation.guidance.currentRoute
        }

    }

    private val windshieldListener = object : WindshieldListener {
        override fun onManoeuvresChanged() {
            upcomingManeuversImpl.value = navigation.guidance.windshield.manoeuvres
        }

        override fun onLaneSignChanged() {
            upcomingLaneSignsImpl.value = navigation.guidance.windshield.laneSigns
        }

        override fun onRoadEventsChanged() = Unit
        override fun onDirectionSignChanged() = Unit
    }

    private val navigationListener = object : NavigationListener {
        override fun onRoutesRequestError(error: Error) {
            navigationRouteStateImpl.value = NavigationState.Error
        }

        override fun onRoutesRequested(requestPoints: MutableList<RequestPoint>) {
            navigationRouteStateImpl.value = NavigationState.Loading
        }

        override fun onAlternativesRequested(p0: DrivingRoute) {
            navigationRouteStateImpl.value = NavigationState.Loading
        }

        override fun onUriResolvingRequested(p0: String) {
            navigationRouteStateImpl.value = NavigationState.Loading
        }

        override fun onRoutesBuilt() {
            navigationRouteStateImpl.value = NavigationState.Success(navigation.routes)
        }

        override fun onResetRoutes() {
            navigationRouteStateImpl.value = NavigationState.Off
        }

    }

    init {
        navigationHolder.navigation
            .onEach { recreateNavigation(it) }
            .launchIn(mainScope)
    }

    override fun location(): StateFlow<Location?> = locationImpl

    override fun requestRoutes(points: List<RequestPoint>) {
        navigation.vehicleOptions = vehicleOptionsManager.vehicleOptions()
        navigation.requestRoutes(
            points,
            navigation.guidance.location?.heading,
            null
        )
    }

    override fun startGuidance(route: DrivingRoute) {
        if (
            navigation.routes.map { it.routeId }.contains(route.routeId)
            || navigation.guidance.currentRoute == null
        ) {
            navigation.startGuidance(route)
        }
        if (settingsManager.simulation.value) {
            navigation.guidance.currentRoute?.let {
                simulationManager.startSimulation(it)
            }
        }
    }

    override fun stopGuidance() {
        routeRequestPointsManager.resetPoints()
        navigation.stopGuidance()
        navigation.resetRoutes()
        simulationManager.stopSimulation()
        backgroundServiceManager.stopService()
        if (settingsManager.restoreGuidanceState.value) {
            settingsManager.serializedNavigation.value = ""
        }
    }

    override fun resetRoutes() {
        navigation.resetRoutes()
    }

    override fun resume() {
        navigation.resume()
        simulationManager.resume()
    }

    override fun suspend() {
        navigation.suspend()
        simulationManager.suspend()
    }

    override fun setAnnotationLanguage(language: AnnotationLanguage) {
        navigation.annotationLanguage = language
    }

    override fun setEnabledAlternatives(isEnabled: Boolean) {
        navigation.guidance.isEnableAlternatives = isEnabled
    }

    override fun setSpeedLimitTolerance(tolerance: Double) {
        navigation.guidance.speedLimitTolerance = tolerance
    }

    override fun setAvoidTolls(isAvoid: Boolean) {
        navigation.isAvoidTolls = isAvoid
    }

    override fun setAvoidUnpaved(isAvoid: Boolean) {
        navigation.isAvoidUnpaved = isAvoid
    }

    override fun setAvoidPoorConditions(isAvoid: Boolean) {
        navigation.isAvoidPoorConditions = isAvoid
    }

    private fun recreateNavigation(newInstance: Navigation) {
        navigation.apply {
            suspend()
            removeListener(navigationListener)
            guidance.removeListener(guidanceListener)
            guidance.windshield.removeListener(windshieldListener)
        }
        navigation = newInstance
        navigation.apply {
            addListener(navigationListener)
            guidance.addListener(guidanceListener)
            guidance.windshield.addListener(windshieldListener)
            resume()

            currentRouteImpl.value = guidance.currentRoute
            roadNameImpl.value = guidance.roadName ?: ""
            upcomingManeuversImpl.value = guidance.windshield.manoeuvres
            upcomingLaneSignsImpl.value = guidance.windshield.laneSigns
            locationImpl.value = guidance.location

            settingsManager.annotationLanguage.value = navigation.annotationLanguage
        }
    }

    companion object {

        private val LOCATION_UPDATE_TIMEOUT = 1.seconds
    }
}

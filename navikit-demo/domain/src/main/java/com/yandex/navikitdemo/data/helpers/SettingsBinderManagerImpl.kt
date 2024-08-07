package com.yandex.navikitdemo.data.helpers

import com.yandex.mapkit.road_events_layer.RoadEventsLayer
import com.yandex.navikitdemo.domain.AnnotationsManager
import com.yandex.navikitdemo.domain.NavigationHolder
import com.yandex.navikitdemo.domain.NavigationLayerManager
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.NavigationStyleManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SimulationManager
import com.yandex.navikitdemo.domain.helpers.BackgroundServiceManager
import com.yandex.navikitdemo.domain.helpers.SettingsBinderManager
import com.yandex.navikitdemo.domain.isGuidanceActive
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ActivityScoped
class SettingsBinderManagerImpl @Inject constructor(
    private val settings: SettingsManager,
    private val simulationManager: SimulationManager,
    private val roadEventsLayer: RoadEventsLayer,
    private val navigationLayerManager: NavigationLayerManager,
    private val navigationStyleManager: NavigationStyleManager,
    private val annotationsManager: AnnotationsManager,
    private val backgroundServiceManager: BackgroundServiceManager,
    private val navigationManager: NavigationManager,
    private val navigationHolder: NavigationHolder,
) : SettingsBinderManager {

    override fun applySettingsChanges(scope: CoroutineScope) {
        with(scope) {
            simulationManager()
            roadEventsLayer()
            navigationLayer()
            navigation()
            camera()
            annotationsManager()
            background()
        }
    }

    private fun CoroutineScope.simulationManager() {
        settings.simulationSpeed.changes()
            .onEach {
                simulationManager.setSimulationSpeed(it.toDouble())
            }
            .launchIn(this)
    }

    private fun CoroutineScope.roadEventsLayer() {
        settings.roadEventsOnRoute
            .map { (tag, setting) -> setting.changes().map { tag to it } }
            .merge()
            .onEach { (tag, visibility) ->
                roadEventsLayer.setRoadEventVisibleOnRoute(tag, visibility)
            }
            .launchIn(this)
    }

    private fun CoroutineScope.navigationLayer() {
        combine(
            settings.jamsMode.changes(),
            settings.balloons.changes(),
            settings.roadEventsOnRouteEnabled.changes(),
            settings.trafficLight.changes(),
            settings.showPredicted.changes()
        ) { jams, balloons, roadEventsOnRoute, trafficLights, predicted ->
            navigationStyleManager.apply {
                currentJamsMode = jams
                balloonsVisibility = balloons
                roadEventsOnRouteVisibility = roadEventsOnRoute
                trafficLightsVisibility = trafficLights
                predictedVisibility = predicted
            }
            navigationLayerManager.refreshStyle()
        }
            .launchIn(this)

        settings.balloonsGeometry.changes()
            .onEach { navigationLayerManager.setShowBalloonsGeometry(it) }
            .launchIn(this)

        navigationHolder.navigation
            .onEach { navigationLayerManager.recreateNavigationLayer() }
            .launchIn(this)
    }

    private fun CoroutineScope.navigation() {
        settings.annotationLanguage.changes()
            .onEach { navigationManager.setAnnotationLanguage(it) }
            .launchIn(this)

        settings.alternatives.changes()
            .onEach { navigationManager.setEnabledAlternatives(it) }
            .launchIn(this)

        settings.speedLimitTolerance.changes()
            .onEach { navigationManager.setSpeedLimitTolerance(it.toDouble()) }
            .launchIn(this)

        combine(
            settings.avoidTolls.changes(),
            settings.avoidUnpaved.changes(),
            settings.avoidPoorConditions.changes(),
        ) { avoidTolls, avoidUnpaved, avoidPoorConditions ->
            navigationManager.apply {
                setAvoidTolls(avoidTolls)
                setAvoidUnpaved(avoidUnpaved)
                setAvoidPoorConditions(avoidPoorConditions)
            }
        }.launchIn(this)
    }

    private fun CoroutineScope.camera() {
        combine(
            settings.autoCamera.changes(),
            settings.autoRotation.changes(),
            settings.autoZoom.changes(),
            settings.zoomOffset.changes(),
        ) { autoCamera, autoRotation, autoZoom, offset ->
            navigationLayerManager.apply {
                setSwitchModesAutomatically(autoCamera)
                setAutoRotation(autoRotation)
                setAutoZoom(autoZoom)
                setFollowingModeZoomOffset(offset)
            }
        }.launchIn(this)
    }

    private fun CoroutineScope.annotationsManager() {
        settings.annotatedEvents
            .map { (event, setting) ->
                setting.changes().map { event to it }
            }
            .merge()
            .onEach { (event, isEnabled) ->
                annotationsManager.setAnnotatedEventEnabled(event, isEnabled)
            }
            .launchIn(this)

        settings.annotatedRoadEvents
            .map { (event, setting) ->
                setting.changes().map { event to it }
            }
            .merge()
            .onEach { (event, isEnabled) ->
                annotationsManager.setAnnotatedRoadEventEnabled(event, isEnabled)
            }
            .launchIn(this)

        settings.muteAnnotations.changes()
            .onEach { annotationsManager.setAnnotationsEnabled(!it) }
            .launchIn(this)
    }

    fun CoroutineScope.background() {
        settings.background.changes()
            .onEach {
                if (it && navigationManager.isGuidanceActive) {
                    backgroundServiceManager.startService()
                } else {
                    backgroundServiceManager.stopService()
                }
            }
            .launchIn(this)
    }

}

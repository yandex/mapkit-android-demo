package com.yandex.navigationdemo.data

import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.navigation.automotive.Navigation
import com.yandex.mapkit.navigation.automotive.layer.BalloonView
import com.yandex.mapkit.navigation.automotive.layer.BalloonViewListener
import com.yandex.mapkit.navigation.automotive.layer.NavigationLayer
import com.yandex.mapkit.navigation.automotive.layer.NavigationLayerFactory
import com.yandex.mapkit.navigation.automotive.layer.NavigationLayerListener
import com.yandex.mapkit.navigation.automotive.layer.RouteView
import com.yandex.mapkit.navigation.automotive.layer.RouteViewListener
import com.yandex.mapkit.navigation.automotive.layer.RoutesSource
import com.yandex.mapkit.navigation.guidance_camera.CameraListener
import com.yandex.mapkit.navigation.guidance_camera.CameraMode
import com.yandex.mapkit.road_events_layer.RoadEventsLayer
import com.yandex.navigationdemo.domain.NavigationLayerManager
import com.yandex.navigationdemo.domain.NavigationStyleManager
import com.yandex.navigationdemo.domain.SettingsManager
import com.yandex.navigationdemo.domain.utils.CameraAnimations
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ActivityScoped
class NavigationLayerManagerImpl @Inject constructor(
    private val mapWindow: MapWindow,
    private val roadEventsLayer: RoadEventsLayer,
    private val navigationStyleManager: NavigationStyleManager,
    private val navigation: Navigation,
    private val settings: SettingsManager,
) : NavigationLayerManager {

    private var isInited = false

    private var navigationLayer: NavigationLayer = createLayer()

    private var currentDrivingRoute: DrivingRoute? = navigationLayer.selectedRoute()?.route

    private val routeViewListener = object : RouteViewListener {
        override fun onRouteViewTap(routeView: RouteView) {
            when (navigationLayer.routesSource) {
                RoutesSource.NAVIGATION -> navigationLayer.selectRoute(routeView)
                RoutesSource.GUIDANCE -> navigationLayer.navigation.guidance.switchToRoute(routeView.route)
            }
        }

        override fun onRouteViewsChanged() {
            // If there is no any selected route,
            // chose the first one as selected.
            if (navigationLayer.selectedRoute() != null) return
            val route = navigationLayer.routes.firstOrNull() ?: return
            navigationLayer.selectRoute(route)
        }
    }

    private val maneuverBalloonVisibilityImpl = MutableStateFlow(false)
    override val maneuverBalloonVisibility = maneuverBalloonVisibilityImpl

    private val cameraFollowingModeImpl = MutableStateFlow(false)
    override val cameraFollowingMode: StateFlow<Boolean> = cameraFollowingModeImpl

    private val balloonViewListener = object : BalloonViewListener {
        override fun onBalloonViewTap(balloonView: BalloonView) {
            val route = balloonView.hostRoute
            when (navigationLayer.routesSource) {
                RoutesSource.NAVIGATION -> navigationLayer.selectRoute(navigationLayer.getView(route))
                RoutesSource.GUIDANCE -> if (balloonView.balloon.alternative != null) {
                    navigationLayer.navigation.guidance.switchToRoute(route)
                }
            }
        }

        override fun onBalloonViewsChanged(p0: RouteView) = Unit
        override fun onBalloonVisibilityChanged(balloonView: BalloonView) {
            if (
                navigationLayer.routesSource == RoutesSource.GUIDANCE
                && balloonView.balloon.manoeuvre != null
            ) {
                maneuverBalloonVisibilityImpl.value = balloonView.isIsVisible
            }
        }

        override fun onBalloonContentChanged(p0: BalloonView) = Unit
    }

    private val navigationLayerListener = object : NavigationLayerListener {
        override fun onSelectedRouteChanged() {
            currentDrivingRoute = navigationLayer.selectedRoute()?.route
        }

        override fun onRoutesSourceChanged() = Unit
    }

    private val cameraListener = CameraListener {
        cameraFollowingModeImpl.value = navigationLayer.camera.cameraMode() == CameraMode.FOLLOWING
    }

    override val selectedRoute: DrivingRoute?
        get() = currentDrivingRoute

    override var cameraMode: CameraMode
        get() = navigationLayer.camera.cameraMode()
        set(value) {
            navigationLayer.camera.setCameraMode(value, CameraAnimations.DEFAULT)
        }

    override fun initIfNeeded() {
        if (isInited) return
        isInited = true
        subscribeForNavigationEvents()
    }

    override fun recreateNavigationLayer() {
        navigationLayer.apply {
            removeRouteViewListener(routeViewListener)
            removeBalloonViewListener(balloonViewListener)
            camera.removeListener(cameraListener)
            removeFromMap()
        }
        navigationLayer = createLayer()
        subscribeForNavigationEvents()

        navigationStyleManager.apply {
            currentJamsMode = settings.jamsMode.value
            balloonsVisibility = settings.balloons.value
            roadEventsOnRouteVisibility = settings.roadEventsOnRouteEnabled.value
            trafficLightsVisibility = settings.trafficLight.value
            predictedVisibility = settings.showPredicted.value
        }
        refreshStyle()

        setShowBalloonsGeometry(settings.balloonsGeometry.value)

        val routeView = currentDrivingRoute?.let { navigationLayer.getView(it) }
        if (routeView != null) {
            routeViewListener.onRouteViewTap(routeView)
        }
    }

    override fun refreshStyle() = navigationLayer.refreshStyle()

    override fun setShowBalloonsGeometry(show: Boolean) =
        navigationLayer.setShowBalloonsGeometry(show)

    override fun setSwitchModesAutomatically(enabled: Boolean) {
        navigationLayer.camera.isSwitchModesAutomatically = enabled
    }

    override fun setAutoRotation(enabled: Boolean) {
        navigationLayer.camera.setAutoRotation(enabled, CameraAnimations.DEFAULT)
    }

    override fun setAutoZoom(enabled: Boolean) {
        navigationLayer.camera.setAutoZoom(enabled, CameraAnimations.DEFAULT)
    }

    override fun setFollowingModeZoomOffset(offset: Float) {
        navigationLayer.camera.setFollowingModeZoomOffset(offset, CameraAnimations.DEFAULT)
    }

    override fun setOverviewRect(rect: ScreenRect) {
        navigationLayer.camera.setOverviewRect(rect, null)
    }

    private fun createLayer(): NavigationLayer {
        return NavigationLayerFactory.createNavigationLayer(
            mapWindow, roadEventsLayer, navigationStyleManager, navigation
        )
    }

    private fun subscribeForNavigationEvents() {
        navigationLayer.apply {
            addRouteViewListener(routeViewListener)
            addBalloonViewListener(balloonViewListener)
            addListener(navigationLayerListener)
            camera.addListener(cameraListener)
        }
    }
}

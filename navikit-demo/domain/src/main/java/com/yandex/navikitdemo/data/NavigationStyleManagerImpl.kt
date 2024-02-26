package com.yandex.navikitdemo.data

import com.yandex.mapkit.directions.driving.Flags
import com.yandex.mapkit.navigation.automotive.layer.styling.JamStyle
import com.yandex.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.RouteStyle
import com.yandex.mapkit.navigation.automotive.layer.styling.RouteViewStyleProvider
import com.yandex.mapkit.navigation.automotive.layer.styling.UserPlacemarkStyleProvider
import com.yandex.mapkit.styling.ArrowStyle
import com.yandex.mapkit.styling.PlacemarkStyle
import com.yandex.mapkit.styling.PolylineStyle
import com.yandex.navikitdemo.domain.NavigationStyleManager
import com.yandex.navikitdemo.domain.models.JamsMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationStyleManagerImpl @Inject constructor(
    private val automotiveNavigationStyleProvider: NavigationStyleProvider,
) : NavigationStyleManager {

    private val routeViewStyleProvider = RouteViewStyleProviderImpl()
    private val userPlacemarkStyleProvider = UserPlacemarkStyleProviderImpl()

    override var trafficLightsVisibility: Boolean = true
    override var roadEventsOnRouteVisibility: Boolean = true
    override var balloonsVisibility: Boolean = true
    override var predictedVisibility: Boolean = true
    override var currentJamsMode: JamsMode = JamsMode.ENABLED_FOR_CURRENT_ROUTE

    override fun routeViewStyleProvider(): RouteViewStyleProvider = routeViewStyleProvider

    override fun userPlacemarkStyleProvider(): UserPlacemarkStyleProvider =
        userPlacemarkStyleProvider

    override fun balloonImageProvider() = automotiveNavigationStyleProvider.balloonImageProvider()

    override fun requestPointStyleProvider() =
        automotiveNavigationStyleProvider.requestPointStyleProvider()

    override fun routePinsStyleProvider() = automotiveNavigationStyleProvider.routePinsStyleProvider()

    private inner class RouteViewStyleProviderImpl : RouteViewStyleProvider {
        override fun provideJamStyle(
            flags: Flags,
            isSelected: Boolean,
            isNightMode: Boolean,
            jamStyle: JamStyle
        ) {
            automotiveNavigationStyleProvider.routeViewStyleProvider()
                .provideJamStyle(flags, isSelected, isNightMode, jamStyle)
        }

        override fun providePolylineStyle(
            flags: Flags,
            isSelected: Boolean,
            isNightMode: Boolean,
            polylineStyle: PolylineStyle
        ) {
            automotiveNavigationStyleProvider.routeViewStyleProvider()
                .providePolylineStyle(flags, isSelected, isNightMode, polylineStyle)
        }

        override fun provideManoeuvreStyle(
            flags: Flags,
            isSelected: Boolean,
            isNightMode: Boolean,
            arrowStyle: ArrowStyle
        ) {
            automotiveNavigationStyleProvider.routeViewStyleProvider()
                .provideManoeuvreStyle(flags, isSelected, isNightMode, arrowStyle)
        }

        override fun provideRouteStyle(
            flags: Flags,
            isSelected: Boolean,
            isNightMode: Boolean,
            routeStyle: RouteStyle
        ) {
            automotiveNavigationStyleProvider.routeViewStyleProvider()
                .provideRouteStyle(flags, isSelected, isNightMode, routeStyle)
            val showJams = when (currentJamsMode) {
                JamsMode.DISABLED -> false
                JamsMode.ENABLED_FOR_CURRENT_ROUTE -> isSelected
                JamsMode.ENABLED -> true
            }
            routeStyle.setShowJams(showJams)
            if (!flags.predicted) {
                routeStyle.apply {
                    setShowRoute(true)
                    setShowTrafficLights(trafficLightsVisibility && isSelected)
                    setShowRoadEvents(roadEventsOnRouteVisibility && isSelected)
                    setShowBalloons(balloonsVisibility)
                    setShowManoeuvres(isSelected)
                }
            } else {
                routeStyle.apply {
                    setShowRoute(predictedVisibility)
                    setShowTrafficLights(false)
                    setShowRoadEvents(roadEventsOnRouteVisibility)
                    setShowBalloons(false)
                    setShowManoeuvres(false)
                }
            }
        }
    }

    private inner class UserPlacemarkStyleProviderImpl : UserPlacemarkStyleProvider {
        override fun provideStyle(scaleFactor: Float, isNightMode: Boolean, style: PlacemarkStyle) {
            style.setArrowModel()
        }
    }
}

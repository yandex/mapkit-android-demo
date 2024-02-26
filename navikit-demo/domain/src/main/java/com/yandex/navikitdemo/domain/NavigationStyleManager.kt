package com.yandex.navikitdemo.domain

import com.yandex.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider
import com.yandex.navikitdemo.domain.models.JamsMode

interface NavigationStyleManager : NavigationStyleProvider {
    var trafficLightsVisibility: Boolean
    var roadEventsOnRouteVisibility: Boolean
    var balloonsVisibility: Boolean
    var predictedVisibility: Boolean
    var currentJamsMode: JamsMode
}

package com.yandex.navigationdemo.domain

import com.yandex.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider
import com.yandex.navigationdemo.domain.models.JamsMode

interface NavigationStyleManager : NavigationStyleProvider {
    var trafficLightsVisibility: Boolean
    var roadEventsOnRouteVisibility: Boolean
    var balloonsVisibility: Boolean
    var predictedVisibility: Boolean
    var currentJamsMode: JamsMode
}

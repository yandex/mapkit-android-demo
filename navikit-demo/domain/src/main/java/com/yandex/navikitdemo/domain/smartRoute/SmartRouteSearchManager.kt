package com.yandex.navikitdemo.domain.smartRoute

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.search.FilterCollection
import com.yandex.navikitdemo.domain.models.State
import kotlinx.coroutines.flow.Flow

interface SmartRouteSearchManager {

    fun submitSearch(
        query: String,
        polyline: Polyline,
        filter: FilterCollection
    ): SmartRouteSearchSession

}

interface SmartRouteSearchSession {

    val searchState: Flow<State<List<Point>>>

    fun cancel()
}

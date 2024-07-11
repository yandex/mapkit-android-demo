package com.yandex.navikitdemo.domain

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.search.FilterCollection
import com.yandex.navikitdemo.domain.models.SearchState
import kotlinx.coroutines.flow.Flow

interface SearchManager {

    val searchState: Flow<SearchState>

    fun submitSearch(query: String, point: Point, polyline: Polyline, filter: FilterCollection)

    fun reset()

}

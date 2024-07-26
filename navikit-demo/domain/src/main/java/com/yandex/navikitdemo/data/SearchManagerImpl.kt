package com.yandex.navikitdemo.data

import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.search.FilterCollection
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.navikitdemo.domain.SearchManager
import com.yandex.navikitdemo.domain.models.State
import com.yandex.runtime.Error
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchManagerImpl @Inject constructor() : SearchManager {

    private val searchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private var searchSession: Session? = null
    private val searchStateImpl = MutableStateFlow<State<List<Point>>>(State.Off)

    private val searchSessionListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val items = response.collection.children.mapNotNull {
                it.obj?.geometry?.firstOrNull()?.point ?: return@mapNotNull null
            }
            searchStateImpl.value = State.Success(items)
        }

        override fun onSearchError(error: Error) {
            searchStateImpl.value = State.Error
        }

    }

    override val searchState = searchStateImpl

    override fun submitSearch(query: String, polyline: Polyline, filter: FilterCollection) {
        val searchGeometry = Geometry.fromPolyline(polyline)
        searchSession?.cancel()
        searchSession = searchManager.submit(
            query,
            searchGeometry,
            SEARCH_OPTIONS.setFilters(filter),
            searchSessionListener
        )
        searchStateImpl.value = State.Loading
    }

    override fun reset() {
        searchSession?.cancel()
        searchSession = null
        searchStateImpl.value = State.Off
    }

    companion object {

        private val SEARCH_OPTIONS = SearchOptions()
            .setResultPageSize(32)
            .setSearchTypes(SearchType.BIZ.value)
    }

}

package com.yandex.navikitdemo.data.smartRoute

import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.search.FilterCollection
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteSearchManager
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteSearchSession
import com.yandex.navikitdemo.domain.models.State
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteSearchListener
import com.yandex.runtime.Error
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartRouteSearchManagerImpl @Inject constructor() : SmartRouteSearchManager {

    private val searchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private var smartRouteSearchSession: SmartRouteSearchSessionBinding? = null

    private val searchSessionListener = object : SmartRouteSearchListener {

        override fun onSearchResponse(response: Response) {
            val items = response.collection.children.mapNotNull {
                it.obj?.geometry?.firstOrNull()?.point ?: return@mapNotNull null
            }
            smartRouteSearchSession?.searchState?.value = State.Success(items)
        }

        override fun onSearchError(error: Error) {
            smartRouteSearchSession?.searchState?.value = State.Error
        }

        override fun onSearchCanceled() {
            smartRouteSearchSession?.searchState?.value = State.Off
        }

    }

    override fun submitSearch(
        query: String,
        polyline: Polyline,
        filter: FilterCollection
    ): SmartRouteSearchSession {
        val searchGeometry = Geometry.fromPolyline(polyline)
        smartRouteSearchSession?.cancel()
        val searchSession = searchManager.submit(
            query,
            searchGeometry,
            SEARCH_OPTIONS.setFilters(filter),
            searchSessionListener
        )
        return SmartRouteSearchSessionBinding(searchSession, searchSessionListener).also {
            it.searchState.value = State.Loading
            smartRouteSearchSession = it
        }
    }

    companion object {

        private val SEARCH_OPTIONS = SearchOptions()
            .setResultPageSize(32)
            .setSearchTypes(SearchType.BIZ.value)
    }

}

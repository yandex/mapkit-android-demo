package com.yandex.navikitdemo.data.smartroute

import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.search.FilterCollection
import com.yandex.mapkit.search.FilterCollectionUtils
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.navikitdemo.domain.models.SmartRouteOptions
import com.yandex.navikitdemo.domain.smartroute.SmartRouteSearchFactory
import com.yandex.navikitdemo.domain.utils.submitSearch
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SmartRouteSearchFactoryImpl @Inject constructor(
    private val searchManager: SearchManager
) : SmartRouteSearchFactory {

    override suspend fun getViaForPolyline(
        thresholdPoint: Point,
        polyline: Polyline,
        options: SmartRouteOptions
    ): Result<Point> {
        val query = options.chargingType.vehicle
        val searchOptions = SearchOptions()
            .setResultPageSize(32)
            .setSearchTypes(SearchType.BIZ.value)
            .setFilters(options.filterTypeCollection())

        val searchGeometry = Geometry.fromPolyline(polyline)
        val searchPoint = searchManager.submitSearch(query, searchGeometry, searchOptions)
            .firstOrNull()
            ?.getOrNull()
            ?.minByOrNull { Geo.distance(thresholdPoint, it) }
            ?: return Result.failure(Exception("SearchError"))
        return Result.success(searchPoint)
    }

    private fun SmartRouteOptions.filterTypeCollection(): FilterCollection {
        val connectors = fuelConnectorTypes.map { it.type }
        return FilterCollectionUtils.createFilterCollectionBuilder()
            .also { it.addEnumFilter(chargingType.filter, connectors) }
            .build()
    }

}

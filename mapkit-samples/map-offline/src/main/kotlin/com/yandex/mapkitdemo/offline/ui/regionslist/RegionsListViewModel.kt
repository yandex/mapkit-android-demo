package com.yandex.mapkitdemo.offline.ui.regionslist

import androidx.lifecycle.ViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.offline_cache.Region
import com.yandex.mapkit.offline_cache.RegionState
import com.yandex.mapkitdemo.offline.utils.regionsFlow
import com.yandex.mapkitdemo.offline.utils.regionsUpdatesFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class RegionsListViewModel : ViewModel() {
    private val offlineCacheManager = MapKitFactory.getInstance().offlineCacheManager

    private val query = MutableStateFlow("")

    fun uiState(): Flow<RegionsListUiState> {
        return combine(
            regionsFlow(offlineCacheManager),
            regionsUpdatesFlow(offlineCacheManager).map {}.onStart { emit(Unit) },
            query,
        ) { regions, _, query ->

            val (completed, available) = regions
                .filter { it.name.contains(query, ignoreCase = true) }
                .partition {
                    offlineCacheManager.getState(it.id).isCompleted()
                }

            RegionsListUiState(
                regionsListItems = buildList {
                    if (completed.isNotEmpty()) {
                        add(RegionsListItem.SectionItem("Downloaded"))
                        addAll(completed.process())
                    }
                    if (available.isNotEmpty()) {
                        add(RegionsListItem.SectionItem("Available"))
                        addAll(available.process())
                    }
                }
            )
        }.distinctUntilChanged()
    }

    fun searchRegions(query: String) {
        this.query.value = query
    }

    private fun List<Region>.process() = map {
        RegionsListItem.RegionItem(
            id = it.id,
            name = it.name,
            cities = offlineCacheManager.getCities(it.id),
        )
    }.sortedBy { it.name }
}

private fun RegionState.isCompleted(): Boolean {
    return when (this) {
        RegionState.COMPLETED -> true
        else -> false
    }
}

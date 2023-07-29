package com.yandex.mapkitdemo.search.ui.map

import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point

data class MapUiState(
    val query: String = "",
    val searchState: SearchState = SearchState.Off,
    val suggestState: SuggestState = SuggestState.Off,
)

sealed interface SearchState {
    object Off : SearchState
    object Loading : SearchState
    object Error : SearchState
    data class Success(
        val items: List<SearchResponseItem>,
        val zoomToItems: Boolean,
        val itemsBoundingBox: BoundingBox,
    ) : SearchState
}

data class SearchResponseItem(
    val point: Point,
    val geoObject: GeoObject?,
)

sealed interface SuggestState {
    object Off : SuggestState
    object Loading : SuggestState
    object Error : SuggestState
    data class Success(val items: List<SuggestHolderItem>) : SuggestState
}

fun SearchState.toTextStatus(): String {
    return when (this) {
        SearchState.Error -> "Error"
        SearchState.Loading -> "Loading"
        SearchState.Off -> "Off"
        is SearchState.Success -> "Success"
    }
}

fun SuggestState.toTextStatus(): String {
    return when (this) {
        SuggestState.Error -> "Error"
        SuggestState.Loading -> "Loading"
        SuggestState.Off -> "Off"
        is SuggestState.Success -> "Success"
    }
}

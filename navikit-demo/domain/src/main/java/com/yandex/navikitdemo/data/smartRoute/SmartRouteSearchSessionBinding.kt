package com.yandex.navikitdemo.data.smartRoute

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Session
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteSearchSession
import com.yandex.navikitdemo.domain.models.State
import com.yandex.navikitdemo.domain.smartRoute.SmartRouteSearchListener
import kotlinx.coroutines.flow.MutableStateFlow

internal class SmartRouteSearchSessionBinding(
    private var searchSession: Session? = null,
    private var smartRouteSearchListener: SmartRouteSearchListener? = null
) : SmartRouteSearchSession {

    override val searchState = MutableStateFlow<State<List<Point>>>(State.Off)

    override fun cancel() {
        searchSession?.cancel()
        smartRouteSearchListener?.onSearchCanceled()
        searchSession = null
    }

}
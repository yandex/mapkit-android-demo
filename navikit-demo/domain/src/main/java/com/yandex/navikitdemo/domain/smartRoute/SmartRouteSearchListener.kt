package com.yandex.navikitdemo.domain.smartRoute

import com.yandex.mapkit.search.Session

interface SmartRouteSearchListener : Session.SearchListener {

    fun onSearchCanceled()
}
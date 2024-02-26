package com.yandex.navikitdemo.data.helpers

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.navikitdemo.domain.helpers.MapTapManager
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class MapTapManagerImpl @Inject constructor(
    private val map: Map
) : MapTapManager {

    private var inputListener: InputListener? = null

    private val longTapActionsImpl = MutableSharedFlow<Point>()
    override val longTapActions: Flow<Point> = longTapActionsImpl

    override fun start(scope: CoroutineScope) {
        inputListener?.let {
            map.removeInputListener(it)
        }
        inputListener = null
        val listener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) = Unit
            override fun onMapLongTap(map: Map, point: Point) {
                scope.launch {
                    longTapActionsImpl.emit(point)
                }
            }
        }
        inputListener = listener
        map.addInputListener(listener)
    }
}

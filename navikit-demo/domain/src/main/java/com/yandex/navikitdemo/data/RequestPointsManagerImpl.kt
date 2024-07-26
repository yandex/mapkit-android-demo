package com.yandex.navikitdemo.data

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.navikitdemo.domain.LocationManager
import com.yandex.navikitdemo.domain.RequestPointsManager
import com.yandex.navikitdemo.domain.utils.toRequestPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPointsManagerImpl @Inject constructor(
    private val locationManager: LocationManager,
) : RequestPointsManager {

    private val requestPointsModel = MutableStateFlow(RequestPointsModel())

    override val requestPoints: Flow<List<RequestPoint>> =
        requestPointsModel.map {
            val from = it.from
                ?: locationManager.location().value?.position
                ?: return@map emptyList()
            val to = it.to ?: return@map emptyList()

            createRequestPoints(from, it.via, to)
        }.distinctUntilChanged()

    override fun setFromPoint(point: Point) {
        requestPointsModel.update { it.copy(from = point) }
    }

    override fun setToPoint(point: Point) {
        requestPointsModel.update { it.copy(to = point) }
    }

    override fun addViaPoint(point: Point) {
        requestPointsModel.update { it.copy(via = it.via + point) }
    }

    override fun resetPoints() {
        requestPointsModel.value = RequestPointsModel()
    }

    private data class RequestPointsModel(
        val from: Point? = null,
        val to: Point? = null,
        val via: List<Point> = emptyList(),
    )
}

private fun createRequestPoints(from: Point, via: List<Point>, to: Point): List<RequestPoint> {
    return buildList {
        add(from.toRequestPoint())
        addAll(via.map { it.toRequestPoint() })
        add(to.toRequestPoint())
    }
}

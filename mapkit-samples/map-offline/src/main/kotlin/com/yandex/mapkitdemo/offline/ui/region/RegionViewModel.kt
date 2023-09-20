package com.yandex.mapkitdemo.offline.ui.region

import androidx.lifecycle.ViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.offline_cache.Region
import com.yandex.mapkitdemo.offline.utils.regionsFlow
import com.yandex.mapkitdemo.offline.utils.regionsUpdatesFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class RegionViewModel : ViewModel() {
    private val offlineCacheManager = MapKitFactory.getInstance().offlineCacheManager

    private fun regionFlow(regionId: Int): Flow<Region?> {
        return regionsFlow(offlineCacheManager)
            .map { regions ->
                regions.firstOrNull { it.id == regionId }
            }
    }

    private fun regionUpdatesEvents(regionId: Int): Flow<Unit> {
        return regionsUpdatesFlow(offlineCacheManager)
            .filter { it.id == regionId }
            .map { }
            .onStart { emit(Unit) }
    }

    fun uiState(regionId: Int): Flow<RegionUiState?> {
        return combine(
            regionFlow(regionId),
            regionUpdatesEvents(regionId),
        ) { regionOrNull, _ ->
            val region = regionOrNull ?: return@combine null

            RegionUiState(
                id = region.id,
                name = region.name,
                country = region.country,
                center = region.center.run { latitude to longitude },
                cities = offlineCacheManager.getCities(region.id),
                size = region.size.text,
                downloadProgress = offlineCacheManager.getProgress(region.id),
                parentId = region.parentId,
                state = offlineCacheManager.getState(region.id),
                releaseTime = region.releaseTime.toTimeString(),
                downloadedReleaseTime = offlineCacheManager.getDownloadedReleaseTime(region.id)?.toTimeString(),
            )
        }
    }

    /**
     * Returns true if offline cache downloading started with success, false
     * if there may be not enough available disk space on the device.
     */
    fun startDownload(regionId: Int): Boolean {
        if (offlineCacheManager.mayBeOutOfAvailableSpace(regionId)) return false
        offlineCacheManager.startDownload(regionId)
        return true
    }

    fun stopDownload(regionId: Int) {
        offlineCacheManager.stopDownload(regionId)
    }

    fun pauseDownload(regionId: Int) {
        offlineCacheManager.pauseDownload(regionId)
    }

    fun drop(regionId: Int) {
        offlineCacheManager.drop(regionId)
    }
}

private fun Long.toTimeString(): String? {
    val date = Date(this)
    val format: Format = SimpleDateFormat("dd.MM.yyyy")
    return format.format(date)
}

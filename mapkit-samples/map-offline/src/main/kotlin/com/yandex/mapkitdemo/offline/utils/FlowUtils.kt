package com.yandex.mapkitdemo.offline.utils

import com.yandex.mapkit.offline_cache.OfflineCacheManager
import com.yandex.mapkit.offline_cache.OfflineCacheManager.ErrorListener
import com.yandex.mapkit.offline_cache.Region
import com.yandex.mapkit.offline_cache.RegionListUpdatesListener
import com.yandex.mapkit.offline_cache.RegionListener
import com.yandex.mapkit.offline_cache.RegionState
import com.yandex.runtime.Error
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Convenient wrapper for OfflineCacheManager subscription using kotlin coroutines.
 */
fun regionsFlow(offlineCacheManager: OfflineCacheManager): Flow<List<Region>> = callbackFlow {
    val listener = RegionListUpdatesListener {
        trySendBlocking(offlineCacheManager.regions())
    }
    offlineCacheManager.addRegionListUpdatesListener(listener)
    trySendBlocking(offlineCacheManager.regions())
    awaitClose { offlineCacheManager.removeRegionListUpdatesListener(listener) }
}

sealed class RegionUpdates(val id: Int) {
    data class ProgressChanged(val regionId: Int, val progress: Float) : RegionUpdates(regionId)
    data class StateChanged(val regionId: Int, val state: RegionState) : RegionUpdates(regionId)
}

fun regionsUpdatesFlow(offlineCacheManager: OfflineCacheManager): Flow<RegionUpdates> =
    callbackFlow {
        val listener = object : RegionListener {
            override fun onRegionStateChanged(id: Int) {
                trySendBlocking(RegionUpdates.StateChanged(id, offlineCacheManager.getState(id)))
            }

            override fun onRegionProgress(id: Int) {
                trySendBlocking(
                    RegionUpdates.ProgressChanged(
                        id,
                        offlineCacheManager.getProgress(id)
                    )
                )
            }
        }
        offlineCacheManager.addRegionListener(listener)
        awaitClose { offlineCacheManager.removeRegionListener(listener) }
    }

sealed class OfflineCacheError(val error: Error) {
    data class Manager(private val errorArg: Error) : OfflineCacheError(errorArg)
    data class Region(private val errorArg: Error, val regionId: Int) : OfflineCacheError(errorArg)
}

fun offlineCacheErrorsFlow(offlineCacheManager: OfflineCacheManager): Flow<OfflineCacheError> =
    callbackFlow {
        val listener = object : ErrorListener {
            override fun onError(error: Error) {
                trySendBlocking(OfflineCacheError.Manager(error))
            }

            override fun onRegionError(error: Error, regionId: Int) {
                trySendBlocking(OfflineCacheError.Region(error, regionId))
            }
        }
        offlineCacheManager.addErrorListener(listener)
        awaitClose { offlineCacheManager.removeErrorListener(listener) }
    }

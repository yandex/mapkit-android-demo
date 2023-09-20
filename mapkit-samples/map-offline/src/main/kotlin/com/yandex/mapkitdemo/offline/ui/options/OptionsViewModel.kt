package com.yandex.mapkitdemo.offline.ui.options

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.offline_cache.DataMoveListener
import com.yandex.mapkit.offline_cache.OfflineCacheManager.PathSetterListener
import com.yandex.runtime.Error
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

private const val PROPERTIES_KEY = "propertiesKey"
private const val AUTO_UPDATE_KEY = "autoUpdateEnabled"
private const val ALLOW_CELLULAR_NETWORK_KEY = "allowCellularNetwork"

class OptionsViewModel(context: Context) : ViewModel() {

    private val prefs = context.getSharedPreferences(PROPERTIES_KEY, Context.MODE_PRIVATE)
    private val offlineCacheManager = MapKitFactory.getInstance().offlineCacheManager

    private val state = MutableStateFlow(
        OptionsUiState(
            allowCellularNetwork = prefs.getBoolean(ALLOW_CELLULAR_NETWORK_KEY, false),
            autoUpdateEnabled = prefs.getBoolean(AUTO_UPDATE_KEY, false),
        )
    )

    init {
        state.onEach {
            offlineCacheManager.allowUseCellularNetwork(it.allowCellularNetwork)
            offlineCacheManager.enableAutoUpdate(it.autoUpdateEnabled)

            prefs.edit()
                .putBoolean(ALLOW_CELLULAR_NETWORK_KEY, it.allowCellularNetwork)
                .putBoolean(AUTO_UPDATE_KEY, it.autoUpdateEnabled)
                .apply()
        }.launchIn(viewModelScope)
    }

    fun uiState(): Flow<OptionsUiState> = state

    fun calculateCacheSize(): Flow<Long?> = callbackFlow {
        offlineCacheManager.computeCacheSize { trySendBlocking(it) }
        awaitClose { }
    }

    fun clearCache(): Flow<Unit> = callbackFlow {
        offlineCacheManager.clear { trySendBlocking(Unit) }
        awaitClose { }
    }

    fun requestCachesPath(): Flow<String> = callbackFlow {
        offlineCacheManager.requestPath { trySendBlocking(it) }
        awaitClose { }
    }

    sealed interface MovePathState {
        object Completed : MovePathState
        data class Progress(val value: Int) : MovePathState
        data class Error(val error: com.yandex.runtime.Error) : MovePathState
    }

    fun movePath(path: String): Flow<MovePathState> = callbackFlow {
        offlineCacheManager.moveData(path, object : DataMoveListener {
            override fun onDataMoveProgress(progress: Int) {
                trySendBlocking(MovePathState.Progress(progress))
            }

            override fun onDataMoveCompleted() {
                trySendBlocking(MovePathState.Completed)
            }

            override fun onDataMoveError(error: Error) {
                trySendBlocking(MovePathState.Error(error))
            }
        })
        awaitClose { }
    }

    sealed interface PathSetState {
        object Success : PathSetState
        data class Error(val error: com.yandex.runtime.Error) : PathSetState
    }

    fun switchPath(path: String): Flow<PathSetState> = callbackFlow {
        offlineCacheManager.setCachePath(path, object : PathSetterListener {
            override fun onPathSet() {
                trySendBlocking(PathSetState.Success)
            }

            override fun onPathSetError(error: Error) {
                trySendBlocking(PathSetState.Error(error))
            }
        })
        awaitClose { }
    }

    fun setCellularNetwork(enabled: Boolean) {
        state.update { it.copy(allowCellularNetwork = enabled) }
    }

    fun setAutoUpdateEnabled(enabled: Boolean) {
        state.update { it.copy(autoUpdateEnabled = enabled) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return OptionsViewModel(application) as T
            }
        }
    }
}

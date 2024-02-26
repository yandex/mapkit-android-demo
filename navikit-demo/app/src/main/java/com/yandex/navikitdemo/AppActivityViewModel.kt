package com.yandex.navikitdemo

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.helpers.NavigationClient
import com.yandex.navikitdemo.domain.helpers.NavigationSuspenderManager
import com.yandex.navikitdemo.domain.isGuidanceActive
import com.yandex.navikitdemo.domain.models.StyleMode
import com.yandex.navikitdemo.domain.utils.isNightModeActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AppActivityViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val navigationManager: NavigationManager,
    private val navigationSuspenderManager: NavigationSuspenderManager,
) : ViewModel() {

    fun nightModeActive(resources: Resources): Flow<Boolean> {
        val system = resources.isNightModeActive()
        return settingsManager.styleMode.changes()
            .map {
                when (it) {
                    StyleMode.NIGHT -> true
                    StyleMode.DAY -> false
                    StyleMode.SYSTEM -> system
                }
            }
    }

    fun onResume() {
        navigationManager.resume()
        navigationSuspenderManager.register(NavigationClient.ACTIVITY)
    }

    fun onPause() {
        serializeNavigationIfNeeded()
        navigationSuspenderManager.removeClient(NavigationClient.ACTIVITY)
    }

    private fun serializeNavigationIfNeeded() {
        if (
            navigationManager.isGuidanceActive
            && settingsManager.restoreGuidanceState.value
        ) {
            navigationManager.serializeNavigation()
        }
    }
}

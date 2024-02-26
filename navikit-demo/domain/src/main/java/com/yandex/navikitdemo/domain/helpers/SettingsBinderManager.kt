package com.yandex.navikitdemo.domain.helpers

import kotlinx.coroutines.CoroutineScope

interface SettingsBinderManager {
    fun applySettingsChanges(scope: CoroutineScope)
}

package com.yandex.navigationdemo.domain.helpers

import kotlinx.coroutines.CoroutineScope

interface SettingsBinderManager {
    fun applySettingsChanges(scope: CoroutineScope)
}

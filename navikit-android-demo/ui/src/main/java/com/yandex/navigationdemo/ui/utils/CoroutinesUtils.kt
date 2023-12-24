package com.yandex.navigationdemo.ui.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.subscribe(owner: LifecycleOwner, block: (T) -> Unit = {}) {
    this.flowWithLifecycle(owner.lifecycle)
        .onEach { block(it) }
        .launchIn(owner.lifecycleScope)
}

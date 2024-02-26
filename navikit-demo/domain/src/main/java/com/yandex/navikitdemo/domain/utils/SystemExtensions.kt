package com.yandex.navikitdemo.domain.utils

import android.content.res.Configuration
import android.content.res.Resources

fun Resources.isNightModeActive(): Boolean {
    return when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }
}

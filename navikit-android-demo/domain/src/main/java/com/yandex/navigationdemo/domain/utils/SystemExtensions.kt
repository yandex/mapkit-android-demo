package com.yandex.navigationdemo.domain.utils

import android.content.res.Configuration
import android.content.res.Resources

fun Resources.isNightModeActive(): Boolean {
    return when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }
}

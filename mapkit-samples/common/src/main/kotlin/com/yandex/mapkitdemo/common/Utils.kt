package com.yandex.mapkitdemo.common

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.yandex.mapkitdemo.R

typealias CommonColors = R.color
typealias CommonDrawables = R.drawable
typealias CommonId = R.id

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun <T : View, V> T.goneOrRun(value: V?, block: T.(V) -> Unit) {
    this.isVisible = value != null
    if (value != null) {
        this.block(value)
    }
}

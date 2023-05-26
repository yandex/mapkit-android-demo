package com.yandex.mapkitdemo.common

import android.content.Context
import android.widget.Toast
import com.yandex.mapkitdemo.R

typealias CommonColors = R.color
typealias CommonDrawables = R.drawable

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

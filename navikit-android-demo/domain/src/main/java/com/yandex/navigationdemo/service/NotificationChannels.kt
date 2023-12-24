package com.yandex.navigationdemo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

object NotificationChannels {
    const val BG_GUIDANCE_CHANNEL_ID = "background_guidance_channel"

    fun initChannels(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(channel(BG_GUIDANCE_CHANNEL_ID))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun channel(
        name: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ): NotificationChannel {
        return NotificationChannel(name, name, importance)
    }
}

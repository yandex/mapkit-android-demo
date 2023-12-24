package com.yandex.navigationdemo.data.helpers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.yandex.navigationdemo.domain.helpers.BackgroundServiceManager
import com.yandex.navigationdemo.service.BackgroundGuidanceService
import com.yandex.navigationdemo.service.NotificationChannels.initChannels
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundServiceManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : BackgroundServiceManager {

    init {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initChannels(manager)
    }

    private val intent = Intent(context, BackgroundGuidanceService::class.java)

    override fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun stopService() {
        context.stopService(intent)
    }
}

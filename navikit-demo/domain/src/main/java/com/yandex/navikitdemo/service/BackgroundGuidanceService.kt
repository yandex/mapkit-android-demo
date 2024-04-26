package com.yandex.navikitdemo.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.yandex.navikitdemo.domain.NavigationManager
import com.yandex.navikitdemo.domain.R
import com.yandex.navikitdemo.domain.helpers.NavigationClient
import com.yandex.navikitdemo.domain.helpers.NavigationSuspenderManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val BG_NOTIFICATION_ID = 10

@AndroidEntryPoint
class BackgroundGuidanceService : Service() {

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var navigationSuspenderManager: NavigationSuspenderManager

    private val notificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(this, NotificationChannels.BG_GUIDANCE_CHANNEL_ID)
            .setContentTitle("Background guidance running")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.badge_navigation)

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_NOT_STICKY

    override fun onCreate() {
        super.onCreate()
        startForeground(BG_NOTIFICATION_ID, notificationBuilder.build())
        navigationManager.resume()
        navigationSuspenderManager.register(NavigationClient.BACKGROUND_SERVICE)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH)
        }
        stopSelf()
    }

    override fun onDestroy() {
        navigationSuspenderManager.removeClient(NavigationClient.BACKGROUND_SERVICE)
        notificationManager.cancelAll()
        super.onDestroy()
    }
}

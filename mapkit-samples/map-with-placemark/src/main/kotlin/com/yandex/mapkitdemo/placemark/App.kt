package com.yandex.mapkitdemo.placemark

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Reading API key from BuildConfig.
        // Do not forget to add your MAPKIT_API_KEY property to local.properties file.
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}

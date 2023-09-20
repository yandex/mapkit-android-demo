package com.yandex.mapkitdemo;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class MainApplication extends Application {
    /**
     * Replace "your_api_key" with a valid developer key.
     */
    private final String MAPKIT_API_KEY = "your_api_key";

    @Override
    public void onCreate() {
        super.onCreate();
        // Set the api key before calling initialize on MapKitFactory.
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }
}

package com.yandex.mapkitdemo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);
        // Initialize the library to load required native libraries.
        // Warning! It's heavy operation
        MapKitFactory.initialize(this);
    }
}

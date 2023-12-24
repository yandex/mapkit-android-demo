package com.yandex.navigationdemo.ui.settings.settingslist

import android.content.Context
import com.yandex.navigationdemo.ui.R

enum class SettingsScreen {
    START,
    VEHICLE_OPTIONS,
    ROAD_EVENTS,
    ROAD_EVENTS_ON_ROUTE,
    SOUND_ANNOTATIONS,
    ANNOTATED_EVENTS,
    DRIVING_OPTIONS,
    CAMERA,
    MAP,
    SIMULATION,
    GUIDANCE,
}

fun SettingsScreen.toScreenName(context: Context): String {
    return when (this) {
        SettingsScreen.START -> R.string.settings_screen_settings
        SettingsScreen.VEHICLE_OPTIONS -> R.string.settings_screen_vehicle_options
        SettingsScreen.ROAD_EVENTS -> R.string.settings_screen_road_events
        SettingsScreen.ROAD_EVENTS_ON_ROUTE -> R.string.settings_screen_road_events_route
        SettingsScreen.SOUND_ANNOTATIONS -> R.string.settings_screen_sound_annotations
        SettingsScreen.ANNOTATED_EVENTS -> R.string.settings_screen_annotated_events
        SettingsScreen.DRIVING_OPTIONS -> R.string.settings_screen_driving_options
        SettingsScreen.CAMERA -> R.string.settings_screen_camera
        SettingsScreen.MAP -> R.string.settings_screen_map
        SettingsScreen.SIMULATION -> R.string.settings_screen_simulation
        SettingsScreen.GUIDANCE -> R.string.settings_screen_guidance
    }.let { context.getString(it) }
}

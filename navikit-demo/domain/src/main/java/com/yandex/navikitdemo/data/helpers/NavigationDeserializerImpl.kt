package com.yandex.navikitdemo.data.helpers

import android.content.Context
import android.util.Base64
import android.widget.Toast
import com.yandex.mapkit.navigation.automotive.Navigation
import com.yandex.mapkit.navigation.automotive.NavigationSerialization
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.helpers.NavigationDeserializer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationDeserializerImpl @Inject constructor(
    private val settingsManager: SettingsManager,
    @ApplicationContext private val context: Context,
) : NavigationDeserializer {

    override fun deserializeNavigationFromSettings(): Navigation? {
        val serializedNavigation = settingsManager.serializedNavigation.value
        if (serializedNavigation.isEmpty()) return null

        return runCatching {
            val data = Base64.decode(serializedNavigation, Base64.DEFAULT)
            NavigationSerialization.deserialize(data)
        }.getOrElse {
            Toast.makeText(context, "Navigation deserialization failed", Toast.LENGTH_SHORT).show()
            null
        }
    }
}

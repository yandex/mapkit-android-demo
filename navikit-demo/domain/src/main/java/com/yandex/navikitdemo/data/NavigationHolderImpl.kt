package com.yandex.navikitdemo.data

import android.content.Context
import android.util.Base64
import android.widget.Toast
import com.yandex.mapkit.navigation.automotive.Navigation
import com.yandex.mapkit.navigation.automotive.NavigationSerialization
import com.yandex.navikitdemo.domain.NavigationHolder
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.helpers.NavigationDeserializer
import com.yandex.navikitdemo.domain.helpers.NavigationFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationHolderImpl @Inject constructor(
    navigationFactory: NavigationFactory,
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val navigationDeserializer: NavigationDeserializer,
) : NavigationHolder {

    private val navigationImpl = MutableStateFlow(navigationFactory.create())
    override val navigation: StateFlow<Navigation> = navigationImpl

    override fun serialize() {
        val serialized = NavigationSerialization.serialize(navigationImpl.value)
        settingsManager.serializedNavigation.value =
            Base64.encodeToString(serialized, Base64.DEFAULT)
        Toast.makeText(context, "Navigation was serialized", Toast.LENGTH_SHORT).show()
    }

    override fun deserialize() {
        val navigation = navigationDeserializer.deserializeNavigationFromSettings()
        if (navigation == null) {
            Toast.makeText(context, "Can't deserialize Navigation", Toast.LENGTH_SHORT).show()
            return
        }
        navigationImpl.value = navigation
    }
}

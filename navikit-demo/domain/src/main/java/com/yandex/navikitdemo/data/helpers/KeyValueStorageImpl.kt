package com.yandex.navikitdemo.data.helpers

import android.content.Context
import com.yandex.navikitdemo.domain.helpers.KeyValueStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val SHARED_PREF_KEY = "com.yandex.navikitdemo.settings"

@Singleton
class KeyValueStorageImpl @Inject constructor(
    @ApplicationContext context: Context,
) : KeyValueStorage {

    private val prefs = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

    override fun putString(key: String, value: String) {
        prefs.edit()
            .putString(key, value)
            .apply()
    }

    override fun readString(key: String, default: String): String {
        return prefs.getString(key, default) ?: default
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit()
            .putBoolean(key, value)
            .apply()
    }

    override fun readBoolean(key: String, default: Boolean): Boolean {
        return prefs.getBoolean(key, default)
    }

    override fun putFloat(key: String, value: Float) {
        prefs.edit()
            .putFloat(key, value)
            .apply()
    }

    override fun readFloat(key: String, default: Float): Float {
        return prefs.getFloat(key, default)
    }

    override fun <T : Enum<T>> putEnum(key: String, value: T) {
        prefs.edit()
            .putString(key, value.toString())
            .apply()
    }

    override fun <T : Enum<T>> readEnum(key: String, default: T, classItem: Class<T>): T {
        val string = prefs.getString(key, "")
        if (string.isNullOrEmpty()) return default
        return classItem.enumConstants?.find { it.name == string } ?: default
    }

    override fun <T : Enum<T>> putEnumSet(key: String, values: Set<T>) {
        val stringsSet = values.map { it.toString() }.toSet()
        prefs.edit()
            .putStringSet(key, stringsSet)
            .apply()
    }

    override fun <T : Enum<T>> readEnumSet(key: String, default: Set<T>, classItem: Class<T>): Set<T> {
        val stringSet = prefs.getStringSet(key, emptySet())
        if (stringSet.isNullOrEmpty()) return default
        val enumConstants = classItem.enumConstants
        return stringSet.mapNotNull { value ->
            enumConstants?.firstOrNull { it.name == value }
        }.toSet().ifEmpty { default }
    }

}

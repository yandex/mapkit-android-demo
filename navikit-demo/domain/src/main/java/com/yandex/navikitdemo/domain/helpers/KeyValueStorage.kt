package com.yandex.navikitdemo.domain.helpers

interface KeyValueStorage {

    fun putString(key: String, value: String)
    fun readString(key: String, default: String): String

    fun putBoolean(key: String, value: Boolean)
    fun readBoolean(key: String, default: Boolean): Boolean

    fun putFloat(key: String, value: Float)
    fun readFloat(key: String, default: Float): Float

    fun <T : Enum<T>> putEnum(key: String, value: T)
    fun <T : Enum<T>> readEnum(key: String, default: T, classItem: Class<T>): T

    fun <T : Enum<T>> putEnumSet(key: String, values: Set<T>)
    fun <T : Enum<T>> readEnumSet(key: String, default: Set<T>, classItem: Class<T>): Set<T>
}

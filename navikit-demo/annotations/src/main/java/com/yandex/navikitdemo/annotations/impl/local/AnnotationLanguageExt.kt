package com.yandex.navikitdemo.annotations.impl.local

import android.content.res.AssetManager
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.SpeakerPhraseToken
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

private const val SOUND_ASSET_PATH = "sounds"
private const val DEFAULT_PATH = "$SOUND_ASSET_PATH/default/"
private const val FALLBACK_PATH = "$SOUND_ASSET_PATH/en_male/"
private val SOUND_PATHS = mapOf(
    AnnotationLanguage.RUSSIAN to "$SOUND_ASSET_PATH/ru_female/", // You can also use ru_male voice
    AnnotationLanguage.ENGLISH to FALLBACK_PATH,
)


private const val SOUND_NAME = "0.mp3"

internal fun AnnotationLanguage.getSoundPath(): String = path().plus("%s/$SOUND_NAME")
internal fun AnnotationLanguage.getDurations(assets: AssetManager) = getDurations(path(), assets)

internal fun getDefaultDurations(assets: AssetManager) = getDurations(DEFAULT_PATH, assets)
internal fun getDefaultSoundPath(): String = DEFAULT_PATH.plus("%s/$SOUND_NAME")

private fun getDurations(path: String, assets: AssetManager): Map<String, Double> {
    val soundDurations = mutableMapOf<String, Double>()
    val json = openInputStream(path, assets)?.readJson() ?: return soundDurations
    try {
        val jsonObject = JSONObject(json)
        SpeakerPhraseToken.values().map { it.path }.forEach { key ->
            if (jsonObject.has(key)) {
                jsonObject.getJSONObject(key).let { keyObject ->
                    if (keyObject.has(SOUND_NAME)) {
                        keyObject.getDouble(SOUND_NAME).takeIf { it > 0 }?.let { value ->
                            soundDurations[key] = value * 1000
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return soundDurations
}

private fun AnnotationLanguage.path() = SOUND_PATHS[this] ?: FALLBACK_PATH


private fun openInputStream(path: String, assets: AssetManager): InputStream? =
    try {
        assets.open(path.plus("durations.json"))
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

private fun InputStream.readJson(): String? =
    try {
        val size = available()
        val buffer = ByteArray(size)
        read(buffer)
        close()
        String(buffer, Charset.forName("UTF-8"))
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

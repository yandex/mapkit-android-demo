package com.yandex.navikitdemo.annotations.impl

import android.content.Context
import android.speech.tts.TextToSpeech
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.Speaker
import java.util.Locale
import java.util.UUID

internal class TtsSpeaker(
    context: Context,
    private val language: AnnotationLanguage
) : Speaker {

    private var ttsInitialized = false
    private val tts = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            ttsInitialized = true
            updateTtsLanguage()
        }
    }

    override fun reset() {
        tts.stop()
    }

    override fun say(phrase: LocalizedPhrase) {
        tts.speak(phrase.text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }

    override fun duration(phrase: LocalizedPhrase): Double {
        // Heuristic formula for the russian language.
        return phrase.text.length * 0.06 + 0.6
    }

    private fun updateTtsLanguage() {
        tts.language = language.toLocale()
    }

    private fun AnnotationLanguage.toLocale(): Locale {
        return when (this) {
            AnnotationLanguage.RUSSIAN -> Locale("ru", "RU")
            AnnotationLanguage.ENGLISH -> Locale("en", "US")
            AnnotationLanguage.ITALIAN -> Locale("it", "IT")
            AnnotationLanguage.FRENCH -> Locale("fr", "FR")
            AnnotationLanguage.TURKISH -> Locale("tr", "TR")
            AnnotationLanguage.UKRAINIAN -> Locale("uk", "UA")
            AnnotationLanguage.HEBREW -> Locale("he", "IL")
            AnnotationLanguage.SERBIAN -> Locale("sr-Latn", "RS")
            AnnotationLanguage.LATVIAN -> Locale("lv", "LV")
            AnnotationLanguage.FINNISH -> Locale("fi", "FI")
            AnnotationLanguage.ROMANIAN -> Locale("ro", "RO")
            AnnotationLanguage.KYRGYZ -> Locale("ky", "KG")
            AnnotationLanguage.KAZAKH -> Locale("kk", "KZ")
            AnnotationLanguage.LITHUANIAN -> Locale("lt", "LT")
            AnnotationLanguage.ESTONIAN -> Locale("et", "EE")
            AnnotationLanguage.GEORGIAN -> Locale("ka", "GE")
            AnnotationLanguage.UZBEK -> Locale("uz", "UZ")
            AnnotationLanguage.ARMENIAN -> Locale("hy", "AM")
            AnnotationLanguage.AZERBAIJANI -> Locale("az", "AZ")
            AnnotationLanguage.ARABIC -> Locale("ar", "AE")
            AnnotationLanguage.TATAR -> Locale("tt", "RU")
            AnnotationLanguage.PORTUGUESE -> Locale("pt", "PT")
            AnnotationLanguage.LATIN_AMERICAN_SPANISH -> Locale("es-419", "BO")
            AnnotationLanguage.BASHKIR -> Locale("ba", "RU")
        }
    }
}

package com.yandex.navikitdemo.annotations.api

import android.content.Context
import com.yandex.mapkit.annotations.AnnotationLanguage
import com.yandex.mapkit.annotations.Speaker
import com.yandex.navikitdemo.annotations.impl.ToastSpeaker
import com.yandex.navikitdemo.annotations.impl.TtsSpeaker
import com.yandex.navikitdemo.annotations.impl.local.LocalSpeaker

object SpeakerFactory {

    fun createSpeaker(context: Context, settings: SpeakerSettings): Speaker {
        val speaker =
            if (settings.usePreRecordedAnnotations && settings.annotationLanguage in preRecordedLanguages) {
                LocalSpeaker(context, settings.annotationLanguage)
            } else {
                TtsSpeaker(context, settings.annotationLanguage)
            }

        return if (settings.textAnnotations) {
            ToastSpeaker(context, speaker)
        } else {
            speaker
        }
    }

    private val preRecordedLanguages = setOf(
        AnnotationLanguage.ENGLISH,
        AnnotationLanguage.RUSSIAN
    )
}

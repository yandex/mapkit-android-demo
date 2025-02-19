package com.yandex.navikitdemo.annotations.api

import com.yandex.mapkit.annotations.AnnotationLanguage

data class SpeakerSettings(
    val annotationLanguage: AnnotationLanguage,
    val usePreRecordedAnnotations: Boolean,
    val textAnnotations: Boolean,
)

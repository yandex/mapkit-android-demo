package com.yandex.navikitdemo.domain

import com.yandex.navikitdemo.domain.models.AnnotatedEventsType
import com.yandex.navikitdemo.domain.models.AnnotatedRoadEventsType
interface AnnotationsManager {
    fun setAnnotationsEnabled(isEnabled: Boolean)

    fun setAnnotatedEventEnabled(event: AnnotatedEventsType, isEnabled: Boolean)
    fun setAnnotatedRoadEventEnabled(event: AnnotatedRoadEventsType, isEnabled: Boolean)
}

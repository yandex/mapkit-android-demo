package com.yandex.navigationdemo.domain

import com.yandex.navigationdemo.domain.models.AnnotatedEventsType
import com.yandex.navigationdemo.domain.models.AnnotatedRoadEventsType
import kotlinx.coroutines.CoroutineScope

interface AnnotationsManager {
    fun setAnnotationsEnabled(isEnabled: Boolean)

    fun setAnnotatedEventEnabled(event: AnnotatedEventsType, isEnabled: Boolean)
    fun setAnnotatedRoadEventEnabled(event: AnnotatedRoadEventsType, isEnabled: Boolean)
}

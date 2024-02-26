package com.yandex.navikitdemo.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.yandex.mapkit.navigation.automotive.Annotator
import com.yandex.mapkit.navigation.automotive.AnnotatorListener
import com.yandex.navikitdemo.domain.AnnotationsManager
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SpeakerManager
import com.yandex.navikitdemo.domain.models.AnnotatedEventsType
import com.yandex.navikitdemo.domain.models.AnnotatedRoadEventsType
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ActivityScoped
class AnnotationsManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val annotator: Annotator,
    private val speaker: SpeakerManager,
) : AnnotationsManager {

    private val scope = MainScope()

    private val annotatorListener = object : AnnotatorListener {
        override fun manoeuvreAnnotated() {
            Log.d(TAG, "manoeuvreAnnotated")
        }

        override fun roadEventAnnotated() {
            Log.d(TAG, "roadEventAnnotated")
        }

        override fun speedingAnnotated() {
            Log.d(TAG, "speedingAnnotated")
        }

        override fun fasterAlternativeAnnotated() {
            Log.d(TAG, "fasterAlternativeAnnotated")
        }
    }

    init {
        annotator.apply {
            setSpeaker(speaker)
            addListener(annotatorListener)
        }

        speaker.phrases()
            .onEach {
                tryShowAnnotationToast(it)
            }
            .launchIn(scope)
    }

    override fun setAnnotationsEnabled(isEnabled: Boolean) {
        with(annotator) {
            if (isEnabled) unmute() else mute()
        }
    }

    override fun setAnnotatedEventEnabled(event: AnnotatedEventsType, isEnabled: Boolean) {
        annotator.annotatedEvents =
            applyEventAvailabilityToMask(
                event.mapkitEnum.value,
                isEnabled,
                annotator.annotatedEvents
            )
    }

    override fun setAnnotatedRoadEventEnabled(event: AnnotatedRoadEventsType, isEnabled: Boolean) {
        annotator.annotatedRoadEvents =
            applyEventAvailabilityToMask(
                event.mapkitEnum.value,
                isEnabled,
                annotator.annotatedRoadEvents
            )
    }

    private fun applyEventAvailabilityToMask(event: Int, isEnabled: Boolean, mask: Int): Int {
        return if (isEnabled) mask or event else mask and event.inv()
    }

    private fun tryShowAnnotationToast(message: String) {
        if (settingsManager.textAnnotations.value) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private companion object {
        const val TAG = "AnnotationsManagerImpl"
    }
}

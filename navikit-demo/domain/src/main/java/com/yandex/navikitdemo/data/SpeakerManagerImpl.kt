package com.yandex.navikitdemo.data

import android.content.Context
import com.yandex.navikitdemo.annotations.api.SpeakerFactory
import com.yandex.navikitdemo.annotations.api.SpeakerSettings
import com.yandex.navikitdemo.domain.NavigationHolder
import com.yandex.navikitdemo.domain.SettingsManager
import com.yandex.navikitdemo.domain.SpeakerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeakerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settings: SettingsManager,
    private val navigationHolder: NavigationHolder,
) : SpeakerManager {

    override fun startIn(scope: CoroutineScope) {
        navigationHolder.navigation
            .map { it.guidance.annotator }
            .flatMapLatest { annotator ->
                combine(
                    settings.annotationLanguage.changes(),
                    settings.preRecordedAnnotations.changes(),
                    settings.textAnnotations.changes(),
                ) { language, preRecordedAnnotations, textAnnotations ->
                    SpeakerSettings(language, preRecordedAnnotations, textAnnotations)
                }
                    .map {
                        SpeakerFactory.createSpeaker(context.applicationContext, it)
                    }
                    .onEach { speaker ->
                        annotator.setSpeaker(speaker)
                    }
            }
            .launchIn(scope)
    }
}

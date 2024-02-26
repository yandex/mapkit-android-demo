package com.yandex.navikitdemo.domain

import com.yandex.mapkit.annotations.Speaker
import kotlinx.coroutines.flow.Flow

interface SpeakerManager : Speaker {
    fun phrases(): Flow<String>
}

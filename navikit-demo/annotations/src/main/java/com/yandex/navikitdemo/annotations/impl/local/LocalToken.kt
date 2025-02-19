package com.yandex.navikitdemo.annotations.impl.local

import android.net.Uri
import com.yandex.mapkit.annotations.SpeakerPhraseToken

internal data class LocalToken(
    val token: SpeakerPhraseToken,
    val duration: Double,
    val uri: Uri,
)

internal class LocalPhrase(
    val items: List<LocalToken>,
) {
    val duration by lazy { items.sumOf { it.duration } }
}

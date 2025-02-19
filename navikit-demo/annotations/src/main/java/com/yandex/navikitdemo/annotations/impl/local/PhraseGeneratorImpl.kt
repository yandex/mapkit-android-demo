package com.yandex.navikitdemo.annotations.impl.local

import android.net.Uri
import com.yandex.mapkit.annotations.LocalizedPhrase

internal class PhraseGeneratorImpl(
    private val soundDurations: Map<String, Double>,
    private val soundPath: String
) : PhraseGenerator {

    override fun generate(phrase: LocalizedPhrase): LocalPhrase {
        return LocalPhrase(phrase.tokens.mapNotNull { token ->
            LocalToken(
                token,
                soundDurations[token.path] ?: return@mapNotNull null,
                Uri.parse("asset:///${String.format(soundPath, token.path)}")
            )
        }.toList())
    }
}

package com.yandex.navikitdemo.annotations.impl.local

import com.yandex.mapkit.annotations.LocalizedPhrase

internal class PhraseGeneratorCached(
    private val phraseGenerator: PhraseGenerator
) : PhraseGenerator {

    private var lastGenerated: Pair<LocalizedPhrase, LocalPhrase>? = null

    override fun generate(phrase: LocalizedPhrase): LocalPhrase {
        if (lastGenerated?.first?.text == phrase.text) {
            return lastGenerated!!.second
        }
        lastGenerated = phrase to phraseGenerator.generate(phrase)
        return lastGenerated!!.second
    }

}

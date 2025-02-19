package com.yandex.navikitdemo.annotations.impl.local

import com.yandex.mapkit.annotations.LocalizedPhrase

internal interface PhraseGenerator {
    fun generate(phrase: LocalizedPhrase): LocalPhrase
}

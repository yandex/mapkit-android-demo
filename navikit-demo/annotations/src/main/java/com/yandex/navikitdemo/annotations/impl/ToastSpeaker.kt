package com.yandex.navikitdemo.annotations.impl

import android.content.Context
import android.widget.Toast
import com.yandex.mapkit.annotations.LocalizedPhrase
import com.yandex.mapkit.annotations.Speaker

internal class ToastSpeaker(
    private val context: Context,
    private val internalSpeaker: Speaker
) : Speaker {
    override fun reset() {
        internalSpeaker.reset()
    }

    override fun say(phrase: LocalizedPhrase) {
        internalSpeaker.say(phrase)
        Toast.makeText(context, phrase.text, Toast.LENGTH_SHORT).show()
    }

    override fun duration(phrase: LocalizedPhrase): Double = internalSpeaker.duration(phrase)
}

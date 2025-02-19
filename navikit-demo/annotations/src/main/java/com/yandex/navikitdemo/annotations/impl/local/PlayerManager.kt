package com.yandex.navikitdemo.annotations.impl.local

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

internal class PlayerManager(context: Context) {
    private val player: ExoPlayer by lazy { ExoPlayer.Builder(context).build() }

    fun play(queue: List<LocalToken>) {
        player.stop()
        player.clearMediaItems()
        queue.map { MediaItem.fromUri(it.uri) }.forEach(player::addMediaItem)
        player.prepare()
        player.play()
    }

    fun reset() {
        player.stop()
        player.clearMediaItems()
    }
}

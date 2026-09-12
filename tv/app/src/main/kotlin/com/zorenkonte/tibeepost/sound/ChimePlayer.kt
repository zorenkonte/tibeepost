package com.zorenkonte.tibeepost.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.zorenkonte.tibeepost.R

class ChimePlayer(context: Context) {
    private val pool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()
    private var loaded = false
    private var pendingPlay = false
    private val chimeId: Int

    init {
        pool.setOnLoadCompleteListener { _, _, status ->
            loaded = status == 0
            if (loaded && pendingPlay) play()
            pendingPlay = false
        }
        chimeId = pool.load(context, R.raw.chime, 1)
    }

    fun play() {
        if (!loaded) {
            pendingPlay = true
            return
        }
        pool.play(chimeId, 1f, 1f, 1, 0, 1f)
    }

    fun release() = pool.release()

    companion object {
        const val DURATION_MS = 300L
    }
}

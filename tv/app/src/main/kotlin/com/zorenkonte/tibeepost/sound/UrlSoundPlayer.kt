package com.zorenkonte.tibeepost.sound

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

class UrlSoundPlayer {
    private val mainThread = Handler(Looper.getMainLooper())
    private var active: MediaPlayer? = null

    fun play(url: String, onDone: () -> Unit) {
        stop()
        val player = MediaPlayer()
        active = player
        var finished = false
        val finish = Runnable {
            if (finished) return@Runnable
            finished = true
            if (active === player) active = null
            player.release()
            onDone()
        }
        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            player.setDataSource(url)
            player.setOnPreparedListener { it.start() }
            player.setOnCompletionListener { mainThread.post(finish) }
            player.setOnErrorListener { _, _, _ ->
                mainThread.post(finish)
                true
            }
            player.prepareAsync()
            mainThread.postDelayed(finish, MAX_PLAY_MS)
        } catch (_: Exception) {
            finish.run()
        }
    }

    fun stop() {
        active?.let {
            active = null
            try {
                it.release()
            } catch (_: RuntimeException) {
            }
        }
    }

    private companion object {
        const val MAX_PLAY_MS = 30_000L
    }
}

package com.zorenkonte.tibeepost.sound

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.zorenkonte.tibeepost.model.Notification
import com.zorenkonte.tibeepost.model.SoundSpec

class SoundPlayer(context: Context) {
    private val mainThread = Handler(Looper.getMainLooper())
    private val chime = ChimePlayer(context)
    private val urlSound = UrlSoundPlayer()
    private val speaker = Speaker(context)
    private var pendingSpeech: Runnable? = null

    fun play(notification: Notification) {
        cancelPendingSpeech()
        urlSound.stop()
        speaker.stop()
        val speech = if (notification.speak) speechFor(notification) else null
        when (val sound = notification.sound) {
            SoundSpec.None -> speech?.let(speaker::speak)
            SoundSpec.Default -> {
                chime.play()
                speech?.let { scheduleSpeech(it, ChimePlayer.DURATION_MS + SPEECH_GAP_MS) }
            }
            is SoundSpec.Url -> urlSound.play(sound.url) {
                speech?.let { scheduleSpeech(it, SPEECH_GAP_MS) }
            }
        }
    }

    fun preview() = chime.play()

    fun release() {
        cancelPendingSpeech()
        urlSound.stop()
        chime.release()
        speaker.release()
    }

    private fun speechFor(notification: Notification): String =
        listOfNotNull(notification.title?.takeIf { it.isNotBlank() }, notification.message).joinToString(". ")

    private fun scheduleSpeech(text: String, delayMs: Long) {
        val speech = Runnable {
            pendingSpeech = null
            speaker.speak(text)
        }
        pendingSpeech = speech
        mainThread.postDelayed(speech, delayMs)
    }

    private fun cancelPendingSpeech() {
        pendingSpeech?.let(mainThread::removeCallbacks)
        pendingSpeech = null
    }

    private companion object {
        const val SPEECH_GAP_MS = 150L
    }
}

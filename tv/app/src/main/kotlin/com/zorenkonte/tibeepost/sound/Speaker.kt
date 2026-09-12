package com.zorenkonte.tibeepost.sound

import android.content.Context
import android.speech.tts.TextToSpeech

class Speaker(context: Context) {
    private var ready = false
    private var pending: String? = null
    private val engine: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        ready = status == TextToSpeech.SUCCESS
        pending?.let { if (ready) speak(it) }
        pending = null
    }

    fun speak(text: String) {
        if (!ready) {
            pending = text
            return
        }
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tibeepost-${System.nanoTime()}")
    }

    fun stop() {
        pending = null
        if (ready) engine.stop()
    }

    fun release() {
        stop()
        engine.shutdown()
    }
}

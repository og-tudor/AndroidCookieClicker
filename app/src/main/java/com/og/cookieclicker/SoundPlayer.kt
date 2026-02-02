package com.og.cookieclicker

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

object SoundPlayer {
    private var soundPool: SoundPool? = null
    private var buySoundId: Int = -1

    private var isLoaded: Boolean = false

    private fun buildSoundPlayer(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Setăm un listener să știm când e gata încărcarea
        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) isLoaded = true
        }

        // Încărcăm sunetul o singură dată
        buySoundId = soundPool!!.load(context, R.raw.buy, 1)
    }
    fun playBuySound(context: Context) {
        if (soundPool == null) {
            buildSoundPlayer(context)
            // Prima dată când apeși probabil nu se va auzi nimic
            // pentru că se încarcă asincron.
            return
        }

        // Redăm doar dacă încărcarea s-a finalizat
        if (isLoaded && buySoundId != -1) {
            soundPool?.play(buySoundId, 1f, 1f, 0, 0, 1f)
        }
    }
}
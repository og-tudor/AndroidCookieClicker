package com.og.cookieclicker

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

object SoundPlayer {
    private var soundPool: SoundPool? = null

    // Variabile pentru controlul spam-ului
    private var lastPlayTime: Long = 0
    private const val MIN_DELAY_BETWEEN_SOUNDS = 70L // Milisecunde (aprox 14 sunete/secunda max)

    private var gameLoopPlayer: MediaPlayer? = null
    private var buySoundId: Int = -1
    private var isLoaded: Boolean = false
    private var musicPlayer: MediaPlayer? = null

    fun init(context: Context) {
        if (soundPool == null) {
            buildSoundPlayer(context)
        }
    }
    private fun buildSoundPlayer(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) isLoaded = true
        }

        buySoundId = soundPool!!.load(context, R.raw.buy, 1)
    }

    fun playBuySound(context: Context) {
        if (soundPool == null) init(context)

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPlayTime < MIN_DELAY_BETWEEN_SOUNDS) {
            return
        }

        if (isLoaded && buySoundId != -1) {
            // Actualizăm timpul
            lastPlayTime = currentTime

            val randomRate = (95..105).random() / 100f
            soundPool?.play(buySoundId, 0.9f, 0.9f, 1, 0, randomRate)
        }
    }

    fun startGameLoop(context: Context) {
        if (gameLoopPlayer?.isPlaying == true) return

        gameLoopPlayer = MediaPlayer.create(context, R.raw.game_loop)
        gameLoopPlayer?.isLooping = true
        gameLoopPlayer?.setVolume(0f, 0f)
        gameLoopPlayer?.start()
    }

    // Ajusteaza volumul dinamic
    fun updateGameLoopVolume(volume: Float) {
        val safeVolume = volume.coerceIn(0f, 1f)
        gameLoopPlayer?.setVolume(safeVolume, safeVolume)
    }

    fun stopGameLoop() {
        gameLoopPlayer?.stop()
        gameLoopPlayer?.release()
        gameLoopPlayer = null
    }


    fun playEndMusic(context: Context) {
        if (musicPlayer?.isPlaying == true) return

        stopEndMusic()

        musicPlayer = MediaPlayer.create(context, R.raw.end_music)
        musicPlayer?.isLooping = true
        musicPlayer?.start()
    }

    fun stopEndMusic() {
        musicPlayer?.stop()
        musicPlayer?.release()
        musicPlayer = null
    }
}
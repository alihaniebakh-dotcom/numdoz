package ir.numdoz.audio

import android.content.Context
import android.media.MediaPlayer
import java.io.File

private var appContext: Context? = null

fun initAndroidAudio(context: Context) {
    appContext = context.applicationContext
}

private class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var player: MediaPlayer? = null
    private var volume: Float = 0.8f
    private var tempFile: File? = null

    override fun load(bytes: ByteArray) {
        release()
        val file = File(context.cacheDir, "numdoz_music.wav")
        file.writeBytes(bytes)
        tempFile = file
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            isLooping = true
            setVolume(volume, volume)
        }
    }

    override fun play(loop: Boolean) {
        player?.let {
            it.isLooping = loop
            it.start()
        }
    }

    override fun playSfx(bytes: ByteArray, volume: Float) {
        try {
            val file = File(context.cacheDir, "sfx_${System.nanoTime()}.wav")
            file.writeBytes(bytes)
            val mp = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setVolume(volume, volume)
            }
            mp.setOnCompletionListener {
                it.release()
                file.delete()
            }
            mp.start()
        } catch (t: Throwable) {
            println("[NumDoz/audio] android sfx FAILED: ${t.message}")
        }
    }

    override fun pause() { player?.pause() }

    override fun resume() {
        player?.let { if (!it.isPlaying) it.start() }
    }

    override fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
        player?.setVolume(this.volume, this.volume)
    }

    override fun isPlaying(): Boolean = player?.isPlaying == true

    override fun release() {
        try { player?.stop() } catch (_: Throwable) {}
        try { player?.release() } catch (_: Throwable) {}
        player = null
        tempFile?.delete()
        tempFile = null
    }
}

actual fun createPlatformAudioPlayer(): AudioPlayer {
    val ctx = appContext ?: error("Call initAndroidAudio(context) in MainActivity.onCreate first")
    return AndroidAudioPlayer(ctx)
}

package ir.numdoz.audio

import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineEvent
import javax.sound.sampled.SourceDataLine
import kotlin.math.log10

private class DesktopAudioPlayer : AudioPlayer {
    private var volume: Float = 0.8f
    private var musicBytes: ByteArray? = null
    private var streamThread: Thread? = null

    @Volatile private var stopRequested = false
    @Volatile private var pauseRequested = false

    private var line: SourceDataLine? = null

    override fun load(bytes: ByteArray) {
        stopCurrent()
        musicBytes = bytes
        startMusicStream()
    }

    private fun startMusicStream() {
        val bytes = musicBytes ?: return
        stopRequested = false
        pauseRequested = false

        streamThread = Thread {
            var audioLine: SourceDataLine? = null
            try {
                var pcmFormat: AudioFormat? = null

                while (!stopRequested) {
                    val sourceStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes))
                    val srcFormat = sourceStream.format
                    println(
                        "[NumDoz/audio] loop: enc=${srcFormat.encoding} " +
                        "sr=${srcFormat.sampleRate} ch=${srcFormat.channels} " +
                        "bits=${srcFormat.sampleSizeInBits} fs=${srcFormat.frameSize}"
                    )

                    if (pcmFormat == null) {
                        val channels = if (srcFormat.channels > 0) srcFormat.channels else 2
                        val sr = srcFormat.sampleRate
                        pcmFormat = AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            sr,
                            16,
                            channels,
                            channels * 2,
                            sr,
                            false
                        )
                    }

                    val pcmStream = if (
                        srcFormat.encoding == AudioFormat.Encoding.PCM_SIGNED &&
                        srcFormat.frameSize > 0
                    ) {
                        sourceStream
                    } else {
                        println("[NumDoz/audio] converting to PCM: $pcmFormat")
                        AudioSystem.getAudioInputStream(pcmFormat!!, sourceStream)
                    }

                    if (audioLine == null) {
                        audioLine = AudioSystem.getSourceDataLine(pcmFormat!!)
                        audioLine.open(pcmFormat)
                        applyGain(audioLine, volume)
                        audioLine.start()
                        line = audioLine
                        println("[NumDoz/audio] line opened OK")
                    }

                    val buf = ByteArray(8192)
                    var n = pcmStream.read(buf)
                    while (n > 0 && !stopRequested) {
                        if (pauseRequested) {
                            Thread.sleep(50)
                            continue
                        }
                        audioLine.write(buf, 0, n)
                        n = pcmStream.read(buf)
                    }
                    try { pcmStream.close() } catch (_: Throwable) {}
                }
            } catch (t: Throwable) {
                println("[NumDoz/audio] stream error: ${t.javaClass.simpleName}: ${t.message}")
                t.printStackTrace()
            } finally {
                try { audioLine?.drain() } catch (_: Throwable) {}
                try { audioLine?.stop() } catch (_: Throwable) {}
                try { audioLine?.close() } catch (_: Throwable) {}
                line = null
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

    override fun play(loop: Boolean) {
        // استریم در load() شروع می‌شود — no-op
    }

    override fun playSfx(bytes: ByteArray, volume: Float) {
        try {
            val stream = AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes))
            val c = AudioSystem.getClip()
            c.open(stream)
            applyGainClip(c, volume)
            c.addLineListener { event ->
                if (event.type == LineEvent.Type.STOP) {
                    try { c.close() } catch (_: Throwable) {}
                }
            }
            c.start()
        } catch (t: Throwable) {
            println("[NumDoz/audio] sfx error: ${t.message}")
        }
    }

    override fun pause() { pauseRequested = true }

    override fun resume() { pauseRequested = false }

    override fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
        line?.let { applyGain(it, this.volume) }
    }

    override fun isPlaying(): Boolean = streamThread?.isAlive == true && !pauseRequested

    override fun release() {
        stopCurrent()
    }

    private fun stopCurrent() {
        stopRequested = true
        try { streamThread?.join(2000) } catch (_: Throwable) {}
        streamThread = null
        musicBytes = null
    }

    private fun applyGain(l: SourceDataLine, vol: Float) {
        if (!l.isControlSupported(FloatControl.Type.MASTER_GAIN)) return
        val ctrl = l.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl ?: return
        val gain = if (vol <= 0.001f) -80f
                   else (20.0 * log10(vol.toDouble())).toFloat()
        ctrl.value = gain
    }

    private fun applyGainClip(c: Clip, vol: Float) {
        if (!c.isControlSupported(FloatControl.Type.MASTER_GAIN)) return
        val ctrl = c.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl ?: return
        val gain = if (vol <= 0.001f) -80f
                   else (20.0 * log10(vol.toDouble())).toFloat()
        ctrl.value = gain
    }
}

actual fun createPlatformAudioPlayer(): AudioPlayer = DesktopAudioPlayer()

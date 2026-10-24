package ir.numdoz.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import ir.numdoz.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

enum class Sfx {
    CLICK,
    TYPE,
    CORRECT,
    WRONG,
    HINT,
    LEVEL_COMPLETE
}

class AudioManager(
    private val musicPlayer: AudioPlayer,
    private val sfxPlayer: AudioPlayer,
    private val baseMusicVolume: Float = 0.75f,
    private val baseSfxVolume: Float = 0.55f
) {
    private val sfxCache = mutableMapOf<Sfx, ByteArray>()
    private var pausedByBackground = false

    var isMuted by mutableStateOf(false)
        private set

    fun cacheSfx(type: Sfx, bytes: ByteArray) {
        sfxCache[type] = bytes
    }

    fun startMusic(bytes: ByteArray) {
        try {
            println("[NumDoz/audio] startMusic: ${bytes.size} bytes")
            musicPlayer.load(bytes)
            musicPlayer.setVolume(if (isMuted) 0f else baseMusicVolume)
            musicPlayer.play(loop = true)
        } catch (t: Throwable) {
            println("[NumDoz/audio] startMusic FAILED: ${t.message}")
            t.printStackTrace()
        }
    }

    fun playSfx(type: Sfx) {
        if (isMuted) return
        val bytes = sfxCache[type] ?: return
        try {
            sfxPlayer.playSfx(bytes, baseSfxVolume)
        } catch (t: Throwable) {
            println("[NumDoz/audio] playSfx($type) FAILED: ${t.message}")
        }
    }

    fun toggleMute() {
        isMuted = !isMuted
        musicPlayer.setVolume(if (isMuted) 0f else baseMusicVolume)
    }

    // ── Lifecycle (background / foreground) ──
    fun pauseForBackground() {
        if (pausedByBackground) return
        pausedByBackground = true
        try { musicPlayer.pause() } catch (_: Throwable) {}
    }

    fun resumeFromBackground() {
        if (!pausedByBackground) return
        pausedByBackground = false
        try { musicPlayer.resume() } catch (_: Throwable) {}
    }

    fun release() {
        try { LifecycleBus.unregister() } catch (_: Throwable) {}
        musicPlayer.release()
        sfxPlayer.release()
    }
}

val LocalAudio = staticCompositionLocalOf<AudioManager> {
    error("AudioManager not provided")
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun rememberAudioManager(): AudioManager {
    val musicPlayer = remember { createPlatformAudioPlayer() }
    val sfxPlayer = remember { createPlatformAudioPlayer() }
    val manager = remember { AudioManager(musicPlayer, sfxPlayer) }

    // Register lifecycle handlers so pausing the app pauses music
    DisposableEffect(manager) {
        LifecycleBus.register(
            onPause = { manager.pauseForBackground() },
            onResume = { manager.resumeFromBackground() }
        )
        onDispose {
            manager.release()
        }
    }

    LaunchedEffect(Unit) {
        val sfxList = listOf(
            Sfx.CLICK to "files/click.wav",
            Sfx.TYPE to "files/type.wav",
            Sfx.CORRECT to "files/correct.wav",
            Sfx.WRONG to "files/wrong.wav",
            Sfx.HINT to "files/hint.wav",
            Sfx.LEVEL_COMPLETE to "files/level_complete.wav"
        )
        for ((type, path) in sfxList) {
            try {
                manager.cacheSfx(type, Res.readBytes(path))
            } catch (t: Throwable) {
                println("[NumDoz/audio] sfx cache failed: $path — ${t.message}")
            }
        }

        val musicCandidates = listOf("files/calm.mp3", "files/calm.wav")
        for (path in musicCandidates) {
            try {
                val bytes = Res.readBytes(path)
                println("[NumDoz/audio] music file: $path (${bytes.size} bytes)")
                manager.startMusic(bytes)
                return@LaunchedEffect
            } catch (t: Throwable) {
                println("[NumDoz/audio] $path not available: ${t.message}")
            }
        }
        println("[NumDoz/audio] ⚠️  NO music file found — silent mode")
    }

    return manager
}

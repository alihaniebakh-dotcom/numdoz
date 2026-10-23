package ir.numdoz.audio

interface AudioPlayer {
    fun load(bytes: ByteArray)
    fun play(loop: Boolean = true)
    fun playSfx(bytes: ByteArray, volume: Float)
    fun pause()
    fun resume()
    fun setVolume(volume: Float)
    fun isPlaying(): Boolean
    fun release()
}

expect fun createPlatformAudioPlayer(): AudioPlayer

package ir.numdoz.audio

/**
 * Global lifecycle bridge.
 * Platform entry points (MainActivity on Android, WindowAdapter on Desktop)
 * call notifyPaused()/notifyResumed(); AudioManager registers handlers.
 */
object LifecycleBus {
    private var onPauseHandler: (() -> Unit)? = null
    private var onResumeHandler: (() -> Unit)? = null

    fun register(onPause: () -> Unit, onResume: () -> Unit) {
        onPauseHandler = onPause
        onResumeHandler = onResume
    }

    fun unregister() {
        onPauseHandler = null
        onResumeHandler = null
    }

    fun notifyPaused() {
        try { onPauseHandler?.invoke() } catch (_: Throwable) {}
    }

    fun notifyResumed() {
        try { onResumeHandler?.invoke() } catch (_: Throwable) {}
    }
}

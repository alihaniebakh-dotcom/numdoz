package ir.numdoz

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ir.numdoz.audio.LifecycleBus
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NumDoz — Desktop",
        state = rememberWindowState(width = 420.dp, height = 820.dp)
    ) {
        DisposableEffect(Unit) {
            val listener = object : WindowAdapter() {
                override fun windowIconified(e: WindowEvent?) {
                    // Window minimized → pause music
                    LifecycleBus.notifyPaused()
                }

                override fun windowDeiconified(e: WindowEvent?) {
                    // Window restored → resume music
                    LifecycleBus.notifyResumed()
                }
            }
            window.addWindowListener(listener)
            onDispose {
                window.removeWindowListener(listener)
            }
        }
        App()
    }
}

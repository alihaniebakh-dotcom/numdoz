package ir.numdoz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ir.numdoz.ads.registerAdActivity
import ir.numdoz.audio.LifecycleBus
import ir.numdoz.audio.initAndroidAudio
import ir.numdoz.data.local.initAndroidDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAndroidAudio(this)
        initAndroidDataStore(this)
        registerAdActivity(this)
        setContent {
            App()
        }
    }

    override fun onPause() {
        super.onPause()
        LifecycleBus.notifyPaused()
    }

    override fun onResume() {
        super.onResume()
        LifecycleBus.notifyResumed()
    }
}

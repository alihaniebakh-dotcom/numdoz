package ir.numdoz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.numdoz.ui.components.AnimatedLogo
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: () -> Unit) {
    var shown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(80)
        shown = true
        delay(1900)
        onDone()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 460.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = shown,
                enter = fadeIn(tween(700)) +
                        scaleIn(initialScale = 0.75f, animationSpec = tween(900, easing = FastOutSlowInEasing))
            ) {
                AnimatedLogo(size = 160.dp)
            }

            Spacer(Modifier.height(28.dp))

            AnimatedVisibility(
                visible = shown,
                enter = fadeIn(tween(700, delayMillis = 350)) +
                        slideInVertically(
                            initialOffsetY = { 24 },
                            animationSpec = tween(700, delayMillis = 350, easing = FastOutSlowInEasing)
                        )
            ) {
                Text(
                    text = "NUMDOZ",
                    style = MaterialTheme.typography.displayLarge.copy(letterSpacing = 12.sp),
                    fontWeight = FontWeight.Light,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            AnimatedVisibility(
                visible = shown,
                enter = fadeIn(tween(600, delayMillis = 700))
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 36.dp)
                        .height(1.dp)
                        .background(Color(0x33FFFFFF))
                )
            }

            Spacer(Modifier.height(14.dp))

            AnimatedVisibility(
                visible = shown,
                enter = fadeIn(tween(600, delayMillis = 900))
            ) {
                Text(
                    text = "عددچین",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

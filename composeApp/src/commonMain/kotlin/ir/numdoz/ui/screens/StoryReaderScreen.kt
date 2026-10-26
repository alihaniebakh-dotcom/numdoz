package ir.numdoz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.model.StoryChapter
import ir.numdoz.i18n.Lang
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.toFaNum
import ir.numdoz.ui.theme.BgBase
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun StoryReaderScreen(
    story: StoryChapter,
    onDone: () -> Unit
) {
    val audio = LocalAudio.current
    val lang = LocalLang.current
    val isFa = lang == Lang.FA
    val accent = parseHex(story.accentColorHex) ?: Color(0xFF4FE3E0)

    val title = if (isFa) story.titleFa else story.titleEn
    val body = if (isFa) story.bodyFa else story.bodyEn
    val paragraphs = remember(body) {
        body.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
    }

    var visibleCount by remember { mutableIntStateOf(0) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        paragraphs.forEachIndexed { idx, _ ->
            delay(if (idx == 0) 300L else 500L)
            visibleCount = idx + 1
        }
        delay(400)
        showButton = true
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(BgBase)
            .zIndex(30f)
    ) {
        // Ambient gradient
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height * 0.12f),
                    radius = size.width * 1.3f
                ),
                radius = size.width * 1.3f,
                center = Offset(size.width / 2f, size.height * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width / 2f, size.height * 0.95f),
                    radius = size.width * 1.0f
                ),
                radius = size.width * 1.0f,
                center = Offset(size.width / 2f, size.height * 0.95f)
            )
        }

        // Floating particles
        StoryParticles(accent = accent)

        Column(
            Modifier
                .fillMaxSize()
                .widthIn(max = 500.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 28.dp)
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Chapter badge ──
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(accent.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = labelFor(story.position, isFa),
                        style = MaterialTheme.typography.labelLarge,
                        color = accent,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // ── Title ──
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium.copy(
                    textDirection = if (isFa) TextDirection.Rtl else TextDirection.Ltr
                ),
                color = TextPrimary,
                fontWeight = FontWeight.Light,
                textAlign = if (isFa) TextAlign.Right else TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // ── Decorative line ──
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = if (isFa) Arrangement.End else Arrangement.Start
            ) {
                Box(
                    Modifier
                        .widthIn(min = 44.dp)
                        .height(1.dp)
                        .background(accent.copy(alpha = 0.55f))
                )
            }

            Spacer(Modifier.height(22.dp))

            // ── Body ──
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    paragraphs.forEachIndexed { idx, p ->
                        AnimatedVisibility(
                            visible = idx < visibleCount,
                            enter = fadeIn(
                                animationSpec = tween(900, easing = FastOutSlowInEasing)
                            ) + slideInVertically(
                                initialOffsetY = { 14 },
                                animationSpec = tween(900, easing = FastOutSlowInEasing)
                            )
                        ) {
                            Text(
                                text = p,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    textDirection = if (isFa) TextDirection.Rtl else TextDirection.Ltr
                                ),
                                color = TextSecondary,
                                lineHeight = 30.sp,
                                textAlign = if (isFa) TextAlign.Right else TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(Modifier.height(80.dp))
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, BgBase)
                            )
                        )
                )
            }

            // ── Continue button ──
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(700)) + slideInVertically(
                    initialOffsetY = { 18 },
                    animationSpec = tween(700)
                )
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .drawBehind {
                            val r = size.width * 0.6f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(accent.copy(alpha = 0.35f), Color.Transparent),
                                    center = center,
                                    radius = r
                                ),
                                radius = r,
                                center = center
                            )
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(accent.copy(alpha = 0.90f), accent.copy(alpha = 0.60f))
                            )
                        )
                        .clickable {
                            audio.playSfx(Sfx.CLICK)
                            onDone()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFa) "ادامه" else "Continue",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StoryParticles(accent: Color) {
    val particles = remember {
        List(14) {
            Triple(
                kotlin.random.Random.nextFloat(),
                kotlin.random.Random.nextFloat(),
                0.25f + kotlin.random.Random.nextFloat() * 0.5f
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "storyParticles")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )
    Canvas(Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val x = p.first * size.width
            val y = ((p.second - time * p.third) % 1f + 1f) % 1f * size.height
            drawCircle(
                color = accent.copy(alpha = 0.28f),
                radius = 1.4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private fun labelFor(position: Int, isFa: Boolean): String = when {
    position == 0 -> if (isFa) "پیش‌درآمد" else "PROLOGUE"
    position == 12 -> if (isFa) "پایان سال" else "END OF YEAR"
    else -> if (isFa) "پس از ماه ${toFaNum(position)}" else "AFTER MONTH $position"
}

private fun parseHex(hex: String): Color? = try {
    val h = hex.removePrefix("#")
    if (h.length == 6) Color(0xFF000000 or h.toLong(16)) else null
} catch (_: Throwable) {
    null
}
package ir.numdoz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.Section
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.displayDescription
import ir.numdoz.i18n.displayTitle
import ir.numdoz.ui.components.LevelBadge
import ir.numdoz.ui.components.LevelBadgeState
import ir.numdoz.ui.components.LogoShape
import ir.numdoz.ui.components.ShapeLogo
import ir.numdoz.ui.components.shapeForMonth
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary

@Composable
fun MonthDaysScreen(
    yearId: String,
    chapterId: String,
    sectionId: String,
    dataStore: DataStore,
    onTintChange: (Color?) -> Unit,
    onBack: () -> Unit,
    onLevelClick: (levelId: String) -> Unit
) {
    val audio = LocalAudio.current
    val accent = LocalAccent.current
    val lang = LocalLang.current

    var section by remember { mutableStateOf<Section?>(null) }
    var levels by remember { mutableStateOf<List<Level>>(emptyList()) }
    var progress by remember { mutableStateOf<Map<String, LevelProgress>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(sectionId) {
        try {
            section = dataStore.getSections(chapterId).find { it.id == sectionId }
            section?.accentColorHex?.let { hex -> parseHex(hex)?.let { onTintChange(it) } }
            val lv = dataStore.getLevels(sectionId)
            levels = lv
            val prog = mutableMapOf<String, LevelProgress>()
            for (l in lv) dataStore.getLevelProgress(l.id)?.let { prog[l.id] = it }
            progress = prog
        } catch (t: Throwable) {
            println("[NumDoz/ui] MonthDays error: ${t.message}")
        } finally { loading = false }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { audio.playSfx(Sfx.CLICK); onBack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
            }

            section?.let { s ->
                val mColor = parseHex(s.accentColorHex) ?: accent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    mColor.copy(alpha = 0.10f),
                                    Color(0xFF0D0D18).copy(alpha = 0.5f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ShapeLogo(
                        logoName = s.logoName,
                        shape = shapeForMonth(s.logoName),
                        accent = mColor,
                        size = 52.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.displayTitle(lang),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = s.displayDescription(lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            when {
                loading -> Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = accent, strokeWidth = 2.dp) }
                else -> {
                    val monthColor = section?.accentColorHex?.let { parseHex(it) } ?: accent
                    val monthShape: LogoShape = section?.logoName?.let { shapeForMonth(it) } ?: LogoShape.CIRCLE
                    val monthLogo = section?.logoName

                    // ★ 5 rows × 6 cols filling the remaining space
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (row in 0 until 5) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (col in 0 until 6) {
                                    val index = row * 6 + col
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .padding(2.dp)
                                    ) {
                                        if (index < levels.size) {
                                            val lv = levels[index]
                                            val p = progress[lv.id]
                                            val unlocked = index == 0 ||
                                                progress[levels[index - 1].id]?.completed == true
                                            val state = when {
                                                p?.completed == true -> LevelBadgeState.COMPLETED
                                                unlocked -> LevelBadgeState.AVAILABLE
                                                else -> LevelBadgeState.LOCKED
                                            }
                                            LevelBadge(
                                                number = index + 1,
                                                state = state,
                                                accent = monthColor,
                                                shape = monthShape,
                                                logoName = monthLogo,
                                                stars = p?.stars ?: 0,
                                                onClick = {
                                                    audio.playSfx(Sfx.CLICK)
                                                    onLevelClick(lv.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun parseHex(hex: String): Color? = try {
    val h = hex.removePrefix("#")
    if (h.length == 6) Color(0xFF000000 or h.toLong(16)) else null
} catch (_: Throwable) { null }

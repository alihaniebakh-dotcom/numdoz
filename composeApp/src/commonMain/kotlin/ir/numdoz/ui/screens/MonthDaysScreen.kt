package ir.numdoz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.Section
import ir.numdoz.ui.components.LevelBadge
import ir.numdoz.ui.components.LevelBadgeState
import ir.numdoz.ui.components.LogoCircle
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.TextMuted
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

    var section by remember { mutableStateOf<Section?>(null) }
    var levels by remember { mutableStateOf<List<Level>>(emptyList()) }
    var progress by remember { mutableStateOf<Map<String, LevelProgress>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(sectionId) {
        try {
            section = dataStore.getSections(chapterId).find { it.id == sectionId }
            section?.accentColorHex?.let { hex ->
                parseHex(hex)?.let { onTintChange(it) }
            }
            val lv = dataStore.getLevels(sectionId)
            levels = lv
            val prog = mutableMapOf<String, LevelProgress>()
            for (l in lv) dataStore.getLevelProgress(l.id)?.let { prog[l.id] = it }
            progress = prog
            println("[NumDoz/ui] MonthDays: ${lv.size} levels")
        } catch (t: Throwable) {
            println("[NumDoz/ui] MonthDays error: ${t.message}")
        } finally { loading = false }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 460.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { audio.playSfx(Sfx.CLICK); onBack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(4.dp))

            section?.let { s ->
                val mColor = parseHex(s.accentColorHex) ?: accent
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(mColor.copy(alpha = 0.06f))
                        .border(1.dp, mColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LogoCircle(logoName = s.logoName, accent = mColor, size = 64.dp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = s.titleFa,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = s.descriptionFa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            when {
                loading -> Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accent, strokeWidth = 2.dp)
                }
                else -> {
                    val mColor = section?.accentColorHex?.let { parseHex(it) } ?: accent
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        var idx = 0
                        while (idx < levels.size) {
                            val rowEnd = minOf(idx + 4, levels.size)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (j in idx until rowEnd) {
                                    val lv = levels[j]
                                    val p = progress[lv.id]
                                    val unlocked = j == 0 ||
                                        progress[levels[j - 1].id]?.completed == true
                                    val state = when {
                                        p?.completed == true -> LevelBadgeState.COMPLETED
                                        unlocked -> LevelBadgeState.AVAILABLE
                                        else -> LevelBadgeState.LOCKED
                                    }
                                    LevelBadge(
                                        number = j + 1,
                                        state = state,
                                        accent = mColor,
                                        onClick = {
                                            audio.playSfx(Sfx.CLICK)
                                            onLevelClick(lv.id)
                                        }
                                    )
                                }
                            }
                            idx = rowEnd
                        }
                        Spacer(Modifier.height(60.dp))
                    }
                }
            }
        }
    }
}

private fun parseHex(hex: String): Color? = try {
    val h = hex.removePrefix("#")
    if (h.length == 6) Color(0xFF000000 or h.toLong(16)) else null
} catch (_: Throwable) { null }

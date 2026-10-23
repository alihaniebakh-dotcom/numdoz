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
import androidx.compose.runtime.rememberCoroutineScope
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
import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.SectionStats
import ir.numdoz.ui.components.ChapterRow
import ir.numdoz.ui.components.LogoCircle
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ChapterMonthsScreen(
    yearId: String,
    chapterId: String,
    dataStore: DataStore,
    onTintChange: (Color?) -> Unit,
    onBack: () -> Unit,
    onMonthClick: (sectionId: String) -> Unit
) {
    val audio = LocalAudio.current
    val accent = LocalAccent.current
    val scope = rememberCoroutineScope()

    var chapter by remember { mutableStateOf<Chapter?>(null) }
    var monthsWithStats by remember { mutableStateOf<List<Pair<Section, SectionStats>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var pendingMonth by remember { mutableStateOf<Section?>(null) }

    LaunchedEffect(chapterId) {
        try {
            chapter = dataStore.getAllChapters().find { it.id == chapterId }
            chapter?.accentColorHex?.let { hex ->
                parseHex(hex)?.let { onTintChange(it) }
            }
            val secs = dataStore.getSections(chapterId)
            monthsWithStats = secs.map { it to dataStore.getStatsForSection(it.id) }
            println("[NumDoz/ui] ChapterMonths: ${secs.size} months")
        } catch (t: Throwable) {
            println("[NumDoz/ui] ChapterMonths error: ${t.message}")
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

            chapter?.let { ch ->
                val chColor = parseHex(ch.accentColorHex) ?: accent
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(chColor.copy(alpha = 0.06f))
                        .border(1.dp, chColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LogoCircle(logoName = ch.logoName, accent = chColor, size = 72.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = ch.titleFa,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = ch.descriptionFa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("ماه‌ها", style = MaterialTheme.typography.labelLarge, color = TextMuted)
            Spacer(Modifier.height(10.dp))

            when {
                loading -> Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accent, strokeWidth = 2.dp)
                }
                chapter == null -> Text("فصل پیدا نشد", color = TextSecondary)
                else -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val chColor = parseHex(chapter!!.accentColorHex) ?: accent
                    monthsWithStats.forEachIndexed { idx, (month, stats) ->
                        val unlocked = idx == 0 ||
                            monthsWithStats.getOrNull(idx - 1)?.let {
                                it.second.completedLevels >= it.second.totalLevels && it.second.totalLevels > 0
                            } == true
                        val monthColor = parseHex(month.accentColorHex) ?: chColor
                        ChapterRow(
                            title = month.titleFa,
                            subtitle = "${stats.completedLevels} از ${stats.totalLevels} روز",
                            accent = monthColor,
                            logoName = month.logoName,
                            locked = !unlocked,
                            onClick = {
                                audio.playSfx(Sfx.CLICK)
                                scope.launch {
                                    if (!dataStore.isIntroSeen("month_${month.id}")) {
                                        pendingMonth = month
                                    } else {
                                        onMonthClick(month.id)
                                    }
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(60.dp))
                }
            }
        }

        pendingMonth?.let { m ->
            IntroDialog(
                logoName = m.logoName,
                accent = parseHex(m.accentColorHex) ?: accent,
                title = m.titleFa,
                subtitle = m.titleEn,
                description = m.descriptionFa,
                buttonLabel = "ورود به ماه",
                onDismiss = {
                    val id = m.id
                    pendingMonth = null
                    scope.launch {
                        dataStore.markIntroSeen("month_$id")
                        onMonthClick(id)
                    }
                }
            )
        }
    }
}

private fun parseHex(hex: String): Color? = try {
    val h = hex.removePrefix("#")
    if (h.length == 6) Color(0xFF000000 or h.toLong(16)) else null
} catch (_: Throwable) { null }

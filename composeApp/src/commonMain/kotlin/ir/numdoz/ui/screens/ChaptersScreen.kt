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
import ir.numdoz.data.model.ChapterStats
import ir.numdoz.data.model.Year
import ir.numdoz.i18n.Lang
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.LocalStrings
import ir.numdoz.i18n.displayDescription
import ir.numdoz.i18n.displayTitle
import ir.numdoz.ui.components.ChapterRow
import ir.numdoz.ui.components.LogoCircle
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ChaptersScreen(
    yearId: String,
    dataStore: DataStore,
    onTintChange: (Color?) -> Unit,
    onBack: () -> Unit,
    onChapterClick: (String) -> Unit
) {
    val audio = LocalAudio.current
    val accent = LocalAccent.current
    val lang = LocalLang.current
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()

    var year by remember { mutableStateOf<Year?>(null) }
    var chaptersWithStats by remember { mutableStateOf<List<Pair<Chapter, ChapterStats>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var pendingChapter by remember { mutableStateOf<Chapter?>(null) }

    LaunchedEffect(yearId) {
        try {
            year = dataStore.getAllYears().find { it.id == yearId }
            year?.accentColorHex?.let { hex -> parseHex(hex)?.let { onTintChange(it) } }
            val chs = dataStore.getChaptersByYear(yearId)
            chaptersWithStats = chs.map { it to dataStore.getStatsForChapter(it.id) }
            println("[NumDoz/ui] ChaptersScreen: ${chs.size} chapters")
        } catch (t: Throwable) {
            println("[NumDoz/ui] ChaptersScreen error: ${t.message}")
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

            year?.let { y ->
                val yColor = parseHex(y.accentColorHex) ?: accent
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(yColor.copy(alpha = 0.06f))
                        .border(1.dp, yColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LogoCircle(logoName = y.logoName, accent = yColor, size = 64.dp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = y.displayTitle(lang),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = y.displayDescription(lang),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(22.dp))

            Text(strings.seasonsLabel, style = MaterialTheme.typography.labelLarge, color = TextMuted)
            Spacer(Modifier.height(10.dp))

            when {
                loading -> Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = accent, strokeWidth = 2.dp) }

                else -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    chaptersWithStats.forEachIndexed { idx, (ch, stats) ->
                        val unlocked = idx == 0 ||
                            chaptersWithStats.getOrNull(idx - 1)?.let {
                                it.second.completedLevels >= it.second.totalLevels && it.second.totalLevels > 0
                            } == true
                        val chColor = parseHex(ch.accentColorHex) ?: accent
                        ChapterRow(
                            title = ch.displayTitle(lang),
                            subtitle = strings.progressLevels(stats.completedLevels, stats.totalLevels),
                            accent = chColor,
                            logoName = ch.logoName,
                            locked = !unlocked,
                            onClick = {
                                audio.playSfx(Sfx.CLICK)
                                scope.launch {
                                    if (!dataStore.isIntroSeen("chapter_${ch.id}")) {
                                        pendingChapter = ch
                                    } else {
                                        onChapterClick(ch.id)
                                    }
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(60.dp))
                }
            }
        }

        pendingChapter?.let { ch ->
            IntroDialog(
                logoName = ch.logoName,
                accent = parseHex(ch.accentColorHex) ?: accent,
                title = ch.displayTitle(lang),
                subtitle = if (lang == Lang.FA) ch.titleEn else ch.titleFa,
                description = ch.displayDescription(lang),
                buttonLabel = strings.enterSeason,
                onDismiss = {
                    val id = ch.id
                    pendingChapter = null
                    scope.launch {
                        dataStore.markIntroSeen("chapter_$id")
                        onChapterClick(id)
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

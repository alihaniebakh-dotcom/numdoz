package ir.numdoz.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.SectionStats
import ir.numdoz.data.model.StoryChapter
import ir.numdoz.data.seed.StorySeed
import ir.numdoz.i18n.Lang
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.displayDescription
import ir.numdoz.i18n.displayTitle
import ir.numdoz.i18n.toFaNum
import ir.numdoz.ui.components.ChapterRow
import ir.numdoz.ui.components.ShapeLogo
import ir.numdoz.ui.components.shapeForChapter
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
    val lang = LocalLang.current
    val scope = rememberCoroutineScope()

    var chapter by remember { mutableStateOf<Chapter?>(null) }
    var monthsWithStats by remember { mutableStateOf<List<Pair<Section, SectionStats>>>(emptyList()) }
    var readStates by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var pendingStory by remember { mutableStateOf<StoryChapter?>(null) }
    var loading by remember { mutableStateOf(true) }
    var pendingMonth by remember { mutableStateOf<Section?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    fun storyPositionFor(chapterObj: Chapter, sectionIdx: Int): Int =
        chapterObj.orderIndex * 3 + sectionIdx + 1

    LaunchedEffect(chapterId, refreshKey) {
        try {
            val ch = dataStore.getAllChapters().find { it.id == chapterId }
            chapter = ch
            ch?.accentColorHex?.let { hex -> parseHex(hex)?.let { onTintChange(it) } }

            val secs = dataStore.getSections(chapterId)
            monthsWithStats = secs.map { it to dataStore.getStatsForSection(it.id) }

            // read states
            if (ch != null) {
                val states = mutableMapOf<String, Boolean>()
                secs.forEachIndexed { idx, s ->
                    val pos = storyPositionFor(ch, idx)
                    val story = StorySeed.afterMonth(yearId, pos)
                    states[s.id] = story?.let { dataStore.isIntroSeen(it.id) } ?: true
                }
                readStates = states

                // trigger first unread story for a completed month
                for ((idx, pair) in monthsWithStats.withIndex()) {
                    val st = pair.second
                    val completed = st.totalLevels > 0 && st.completedLevels >= st.totalLevels
                    if (completed) {
                        val pos = storyPositionFor(ch, idx)
                        val story = StorySeed.afterMonth(yearId, pos)
                        if (story != null && !dataStore.isIntroSeen(story.id)) {
                            pendingStory = story
                            break
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            println("[NumDoz/ui] ChapterMonths error: ${t.message}")
        } finally {
            loading = false
        }
    }

    fun isUnlocked(idx: Int): Boolean {
        if (idx == 0) return true
        val prevStats = monthsWithStats.getOrNull(idx - 1)?.second ?: return false
        val prevCompleted = prevStats.totalLevels > 0 && prevStats.completedLevels >= prevStats.totalLevels
        if (!prevCompleted) return false
        val prevSec = monthsWithStats[idx - 1].first
        return readStates[prevSec.id] == true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
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
                    Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val r = size.height * 0.9f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(chColor.copy(alpha = 0.12f), Color.Transparent),
                                    center = Offset(size.width / 2f, size.height / 2f),
                                    radius = r
                                ),
                                radius = r, center = Offset(size.width / 2f, size.height / 2f)
                            )
                        }
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    chColor.copy(alpha = 0.08f),
                                    Color(0xFF0D0D18).copy(alpha = 0.6f)
                                )
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ShapeLogo(
                        logoName = ch.logoName,
                        shape = shapeForChapter(ch.logoName),
                        accent = chColor,
                        size = 72.dp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = ch.displayTitle(lang),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = ch.displayDescription(lang),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            Text(
                if (lang == Lang.FA) "ماه‌ها" else "Months",
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted
            )
            Spacer(Modifier.height(10.dp))

            when {
                loading -> Box(
                    Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accent, strokeWidth = 2.dp)
                }
                chapter == null -> Text("—", color = TextSecondary)
                else -> Column(
                    Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val ch = chapter!!
                    val chColor = parseHex(ch.accentColorHex) ?: accent
                    monthsWithStats.forEachIndexed { idx, pair ->
                        val month = pair.first
                        val stats = pair.second
                        val unlocked = isUnlocked(idx)
                        val monthColor = parseHex(month.accentColorHex) ?: chColor
                        ChapterRow(
                            title = month.displayTitle(lang),
                            subtitle = if (lang == Lang.FA)
                                "${toFaNum(stats.completedLevels)} از ${toFaNum(stats.totalLevels)} روز"
                            else
                                "${stats.completedLevels} of ${stats.totalLevels} days",
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
                title = m.displayTitle(lang),
                subtitle = if (lang == Lang.FA) m.titleEn else m.titleFa,
                description = m.displayDescription(lang),
                buttonLabel = if (lang == Lang.FA) "ورود به ماه" else "Enter month",
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

        pendingStory?.let { story ->
            StoryReaderScreen(
                story = story,
                onDone = {
                    scope.launch {
                        dataStore.markIntroSeen(story.id)
                        pendingStory = null
                        refreshKey++
                    }
                }
            )
        }
    }
}

private fun parseHex(hex: String): Color? = try {
    val h = hex.removePrefix("#")
    if (h.length == 6) Color(0xFF000000 or h.toLong(16)) else null
} catch (_: Throwable) {
    null
}
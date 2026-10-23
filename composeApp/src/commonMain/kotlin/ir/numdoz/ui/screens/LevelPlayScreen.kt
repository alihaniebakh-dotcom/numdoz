package ir.numdoz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.UserWallet
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import ir.numdoz.util.currentTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AdPurpose { TIME, HINT }

@Composable
fun LevelPlayScreen(
    levelId: String,
    chapterId: String,
    sectionId: String,
    dataStore: DataStore,
    onTintChange: (Color?) -> Unit,
    onBack: () -> Unit,
    onNext: (nextLevelId: String) -> Unit
) {
    val audio = LocalAudio.current
    val accent = LocalAccent.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var loaded by remember { mutableStateOf(false) }
    var level by remember { mutableStateOf<Level?>(null) }
    var chapter by remember { mutableStateOf<Chapter?>(null) }
    var section by remember { mutableStateOf<Section?>(null) }
    var nextLevelId by remember { mutableStateOf<String?>(null) }
    var wallet by remember { mutableStateOf(UserWallet()) }

    var timeLeft by remember { mutableIntStateOf(180) }
    var input by remember { mutableStateOf("") }
    var hint1Revealed by remember { mutableStateOf(false) }
    var hint2Revealed by remember { mutableStateOf(false) }
    var hintsUsed by remember { mutableIntStateOf(0) }
    var timeExtUsed by remember { mutableIntStateOf(0) }

    var showResult by remember { mutableStateOf(false) }
    var starsEarned by remember { mutableIntStateOf(0) }
    var showTimeoutModal by remember { mutableStateOf(false) }
    var showAdFor by remember { mutableStateOf<AdPurpose?>(null) }
    var adSeconds by remember { mutableIntStateOf(5) }
    var showHintModal by remember { mutableStateOf(false) }
    var wrongTrigger by remember { mutableIntStateOf(0) }
    val shakeOffset = remember { Animatable(0f) }

    val isPaused = showResult || showTimeoutModal || showAdFor != null
    val elapsed = 180 + timeExtUsed * 60 - timeLeft
    val canRevealMore = !hint1Revealed || !hint2Revealed

    LaunchedEffect(levelId) {
        try {
            val lv = dataStore.getLevel(levelId)
            level = lv
            chapter = dataStore.getAllChapters().find { it.id == chapterId }
            if (lv != null) {
                section = dataStore.getSections(chapterId).find { it.id == lv.sectionId }
                val all = dataStore.getLevels(lv.sectionId)
                val idx = all.indexOfFirst { it.id == lv.id }
                nextLevelId = all.getOrNull(idx + 1)?.id
                timeLeft = lv.timeLimitSeconds
                wallet = dataStore.getWallet()
            }
                section?.accentColorHex?.let { hex ->
                    try {
                        val h = hex.removePrefix("#")
                        if (h.length == 6) onTintChange(Color(0xFF000000 or h.toLong(16)))
                    } catch (_: Throwable) { }
                }
            println("[NumDoz/game] loaded: ${lv?.id}")
        } catch (t: Throwable) {
            println("[NumDoz/game] load failed: ${t.message}")
        } finally { loaded = true }
    }

    LaunchedEffect(loaded, isPaused, timeLeft) {
        if (!loaded || isPaused || showResult) return@LaunchedEffect
        if (timeLeft <= 0) {
            showTimeoutModal = true
            audio.playSfx(Sfx.HINT)
            return@LaunchedEffect
        }
        delay(1000)
        timeLeft--
    }

    LaunchedEffect(wrongTrigger) {
        if (wrongTrigger == 0) return@LaunchedEffect
        shakeOffset.animateTo(0f, keyframes {
            durationMillis = 400
            -12f at 40; 12f at 100; -8f at 160; 8f at 220; -4f at 280; 4f at 340; 0f at 400
        })
    }

    LaunchedEffect(showAdFor) {
        val purpose = showAdFor ?: return@LaunchedEffect
        adSeconds = 5
        while (adSeconds > 0) {
            delay(1000)
            adSeconds--
        }
        when (purpose) {
            AdPurpose.TIME -> {
                timeLeft = 60
                timeExtUsed++
            }
            AdPurpose.HINT -> {
                wallet = wallet.copy(
                    freeHints = wallet.freeHints + 1,
                    totalAdsWatched = wallet.totalAdsWatched + 1
                )
                scope.launch { dataStore.updateWallet(wallet) }
            }
        }
        showAdFor = null
        audio.playSfx(Sfx.CLICK)
    }

    fun revealNextHint() {
        when {
            !hint1Revealed -> {
                hint1Revealed = true
                hintsUsed++
                wallet = wallet.copy(freeHints = wallet.freeHints - 1)
                scope.launch { dataStore.updateWallet(wallet) }
                audio.playSfx(Sfx.HINT)
            }
            !hint2Revealed -> {
                hint2Revealed = true
                hintsUsed++
                wallet = wallet.copy(freeHints = wallet.freeHints - 1)
                scope.launch { dataStore.updateWallet(wallet) }
                audio.playSfx(Sfx.HINT)
            }
        }
    }

    fun onHintIconClick() {
        audio.playSfx(Sfx.CLICK)
        if (!canRevealMore) return
        if (wallet.freeHints > 0) {
            revealNextHint()
        } else {
            showHintModal = true
        }
    }

    fun calculateStars(): Int {
        val total = 180 + timeExtUsed * 60
        val f = if (total > 0) timeLeft.toFloat() / total else 0f
        return when {
            hintsUsed == 0 && f >= 0.5f -> 3
            hintsUsed <= 1 && f >= 0.2f -> 2
            else -> 1
        }
    }

    fun submit() {
        val lv = level ?: return
        val a = input.trim()
        if (a.isEmpty()) return
        if (a.equals(lv.correctAnswer, ignoreCase = true)) {
            focusManager.clearFocus()
            starsEarned = calculateStars()
            showResult = true
            audio.playSfx(Sfx.LEVEL_COMPLETE)
            scope.launch {
                dataStore.upsertLevelProgress(
                    LevelProgress(
                        levelId = lv.id, completed = true,
                        bestTimeSeconds = elapsed.coerceAtLeast(1),
                        stars = starsEarned, hintsUsed = hintsUsed,
                        timeExtensionsUsed = timeExtUsed,
                        lastPlayedAt = currentTimeMillis()
                    )
                )
            }
        } else {
            audio.playSfx(Sfx.WRONG)
            wrongTrigger++
        }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { audio.playSfx(Sfx.CLICK); onBack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = TextSecondary)
                }
                Spacer(Modifier.weight(1f))

                // Hint icon — direct reveal if wallet has hints
                IconButton(
                    enabled = canRevealMore,
                    onClick = { onHintIconClick() }
                ) {
                    BadgedBox(badge = {
                        if (wallet.freeHints > 0) {
                            Badge(containerColor = accent, contentColor = Color.White) {
                                Text("${wallet.freeHints}")
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (wallet.freeHints > 0) Icons.Filled.Lightbulb
                                          else Icons.Outlined.Lightbulb,
                            contentDescription = "Hint",
                            tint = when {
                                !canRevealMore -> TextMuted
                                wallet.freeHints > 0 -> accent
                                else -> TextMuted
                            }
                        )
                    }
                }
                Spacer(Modifier.size(4.dp))
                TimerChip(seconds = timeLeft, accent = accent)
            }

            Spacer(Modifier.height(6.dp))

            val subtitle = buildString {
                chapter?.titleFa?.let { append(it) }
                section?.titleFa?.let { append(" · ").append(it) }
                level?.dayNumber?.let { if (it > 0) append(" · روز ").append(it) }
            }
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(Modifier.weight(0.15f))

            level?.let { SequenceDisplay(sequence = it.sequence, accent = accent) }

            Spacer(Modifier.height(24.dp))

            if (level != null) {
                val lv = level!!
                val keyboardType = if (lv.correctAnswer.all { it.isDigit() })
                    KeyboardType.Number else KeyboardType.Text
                val focusRequester = remember { FocusRequester() }

                OutlinedTextField(
                    value = input,
                    onValueChange = { if (it.length <= 12) input = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = shakeOffset.value.dp)
                        .focusRequester(focusRequester),
                    placeholder = { Text("پاسخ…", color = TextMuted) },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center,
                        color = accent,
                        fontWeight = FontWeight.SemiBold
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent.copy(alpha = 0.7f),
                        unfocusedBorderColor = accent.copy(alpha = 0.3f),
                        cursorColor = accent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )

                LaunchedEffect(Unit) { delay(300); focusRequester.requestFocus() }
            }

            // Hints under the input
            if (hint1Revealed) {
                Spacer(Modifier.height(12.dp))
                HintCard(text = level?.hint1Fa ?: "", accent = accent)
            }
            if (hint2Revealed) {
                Spacer(Modifier.height(8.dp))
                HintCard(text = level?.hint2Fa ?: "", accent = accent)
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.85f))
                    .clickable { submit() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Text(
                        text = "تأیید",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.weight(0.3f))
        }

        // Empty-wallet modal (only shown when wallet=0 and user clicks hint)
        if (showHintModal) {
            ModalOverlay {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lightbulb, null, tint = accent, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.size(8.dp))
                        Text(
                            "شما راهنما ندارید",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        "برای دریافت راهنما یکی از گزینه‌های زیر را انتخاب کنید",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    PrimaryActionButton("تماشای تبلیغ — دریافت ۱ راهنما", accent) {
                        showHintModal = false
                        showAdFor = AdPurpose.HINT
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1A1A28))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                            .clickable { /* placeholder — coming soon */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "خرید راهنما (به‌زودی)",
                            color = TextMuted,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    TextButton(onClick = {
                        audio.playSfx(Sfx.CLICK)
                        showHintModal = false
                    }) {
                        Text("بستن", color = TextSecondary)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showResult,
            enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.85f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        ) {
            ResultOverlay(
                stars = starsEarned, elapsed = elapsed, hintsUsed = hintsUsed,
                accent = accent, hasNext = nextLevelId != null,
                onNext = { audio.playSfx(Sfx.CLICK); nextLevelId?.let { onNext(it) } },
                onBack = { audio.playSfx(Sfx.CLICK); onBack() }
            )
        }

        if (showTimeoutModal) {
            ModalOverlay {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("زمان تمام شد", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(
                        "برای ادامه یک تبلیغ ببینید تا ۱ دقیقه اضافه شود",
                        style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center
                    )
                    PrimaryActionButton("تماشای تبلیغ", accent) {
                        showTimeoutModal = false
                        showAdFor = AdPurpose.TIME
                    }
                    TextButton(onClick = { audio.playSfx(Sfx.CLICK); onBack() }) {
                        Text("بازگشت بدون ادامه", color = TextMuted)
                    }
                }
            }
        }

        if (showAdFor != null) {
            ModalOverlay {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("تبلیغ", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                    Text(
                        "شمارش معکوس: $adSeconds",
                        style = MaterialTheme.typography.displayMedium, color = accent, fontWeight = FontWeight.Light
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent.copy(alpha = 0.06f))
                            .border(1.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("جای تبلیغ", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun HintCard(text: String, accent: Color) {
    if (text.isEmpty()) return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.08f))
            .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Lightbulb, null, tint = accent, modifier = Modifier.size(18.dp))
            Text(text = text, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TimerChip(seconds: Int, accent: Color) {
    val danger = seconds <= 30
    val c = if (danger) Color(0xFFF87171) else accent
    val mm = seconds / 60
    val ss = seconds % 60
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(c.copy(alpha = 0.12f))
            .border(1.dp, c.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            "${mm}:${ss.toString().padStart(2, '0')}",
            color = c,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SequenceDisplay(sequence: String, accent: Color) {
    val parts = sequence.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("دنباله را کامل کنید", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            parts.forEachIndexed { i, p ->
                key(i) {
                    val isQ = p == "?"
                    val isLast = i == parts.lastIndex
                    Box(
                        modifier = Modifier
                            .size(if (p.length > 2) 52.dp else 42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isQ) accent.copy(alpha = 0.06f) else accent.copy(alpha = 0.14f))
                            .border(
                                if (isQ) 1.5.dp else 1.dp,
                                if (isQ) accent.copy(alpha = 0.7f) else accent.copy(alpha = 0.35f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p,
                            color = if (isQ) accent else TextPrimary,
                            style = if (isQ) MaterialTheme.typography.headlineMedium
                                    else MaterialTheme.typography.titleLarge,
                            fontWeight = if (isLast) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultOverlay(
    stars: Int, elapsed: Int, hintsUsed: Int, accent: Color,
    hasNext: Boolean, onNext: () -> Unit, onBack: () -> Unit
) {
    ModalOverlay {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("آفرین!", style = MaterialTheme.typography.displayMedium, color = accent, fontWeight = FontWeight.Light)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Icon(
                        Icons.Filled.Star, null,
                        tint = if (i < stars) accent else accent.copy(alpha = 0.18f),
                        modifier = Modifier.size((32 + i * 4).dp)
                    )
                }
            }
            val mm = elapsed / 60
            val ss = elapsed % 60
            Text("زمان: ${mm}:${ss.toString().padStart(2, '0')}", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            if (hintsUsed > 0) Text("$hintsUsed راهنما استفاده شد", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            Spacer(Modifier.height(4.dp))
            if (hasNext) PrimaryActionButton("مرحله‌ی بعد", accent, onNext)
            TextButton(onClick = onBack) { Text("بازگشت به فهرست", color = TextSecondary) }
        }
    }
}

@Composable
private fun ModalOverlay(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xCC08080E)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF12121D))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) { content() }
    }
}

@Composable
private fun PrimaryActionButton(text: String, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth().height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.85f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text, color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

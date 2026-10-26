package ir.numdoz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.numdoz.ads.LocalAdManager
import ir.numdoz.ads.SessionTracker
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.LevelProgress
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.UserWallet
import ir.numdoz.economy.Economy
import ir.numdoz.i18n.Lang
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.LocalStrings
import ir.numdoz.i18n.displayHint1
import ir.numdoz.i18n.displayHint2
import ir.numdoz.i18n.displayTitle
import ir.numdoz.ui.components.CoinIcon
import ir.numdoz.ui.components.HintIcon
import ir.numdoz.ui.components.StarRewardIcon
import ir.numdoz.ui.components.TimeBoostIcon
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import ir.numdoz.util.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val bgScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
    val adManager = LocalAdManager.current
    val baseAccent = LocalAccent.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val lang = LocalLang.current
    val strings = LocalStrings.current

    var loaded by remember { mutableStateOf(false) }
    var level by remember { mutableStateOf<Level?>(null) }
    var chapter by remember { mutableStateOf<Chapter?>(null) }
    var section by remember { mutableStateOf<Section?>(null) }
    var nextLevelId by remember { mutableStateOf<String?>(null) }
    var wallet by remember { mutableStateOf(UserWallet()) }

    var monthAccent by remember { mutableStateOf<Color?>(null) }
    val accent = monthAccent ?: baseAccent
    val capsuleColor = Color(0xFF22D3EE)
    val skipColor = Color(0xFFA78BFA)

    var timeLeft by remember { mutableIntStateOf(60) }
    var input by remember { mutableStateOf("") }
    var hint1Revealed by remember { mutableStateOf(false) }
    var hint2Revealed by remember { mutableStateOf(false) }
    var hintsUsed by remember { mutableIntStateOf(0) }
    var timeExtUsed by remember { mutableIntStateOf(0) }

    var showResult by remember { mutableStateOf(false) }
    var starsEarned by remember { mutableIntStateOf(0) }
    var showTimeoutModal by remember { mutableStateOf(false) }
    var showHintModal by remember { mutableStateOf(false) }
    var showCapsuleModal by remember { mutableStateOf(false) }
    var showSkipModal by remember { mutableStateOf(false) }
    var adLoading by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }
    var starAdLoading by remember { mutableStateOf(false) }
    var starBuyLoading by remember { mutableStateOf(false) }
    var wrongTrigger by remember { mutableIntStateOf(0) }
    val shakeOffset = remember { Animatable(0f) }

    val isPaused = showResult || showTimeoutModal || adLoading || adError != null ||
            showHintModal || showCapsuleModal || showSkipModal || starAdLoading || starBuyLoading
    val elapsed = 60 + timeExtUsed * 60 - timeLeft
    val canRevealMore = !hint1Revealed || !hint2Revealed

    LaunchedEffect(levelId) {
        try {
            val lv = dataStore.getLevel(levelId)
            level = lv
            chapter = dataStore.getAllChapters().find { it.id == chapterId }
            if (lv != null) {
                section = dataStore.getSections(chapterId).find { it.id == lv.sectionId }
                section?.accentColorHex?.let { hex ->
                    try {
                        val h = hex.removePrefix("#")
                        if (h.length == 6) {
                            val c = Color(0xFF000000 or h.toLong(16))
                            monthAccent = c
                            onTintChange(c)
                        }
                    } catch (_: Throwable) { }
                }
                val prog = dataStore.getLevelProgress(lv.id)
                val savedTime = prog?.timeLeftSeconds ?: -1
                val isCompleted = prog?.completed == true
                timeLeft = when {
                    isCompleted -> lv.timeLimitSeconds
                    savedTime > 0 -> savedTime
                    savedTime == 0 -> 0
                    else -> lv.timeLimitSeconds
                }
                hintsUsed = prog?.hintsUsed ?: 0
                timeExtUsed = prog?.timeExtensionsUsed ?: 0
                val all = dataStore.getLevels(lv.sectionId)
                val idx = all.indexOfFirst { it.id == lv.id }
                nextLevelId = all.getOrNull(idx + 1)?.id
                wallet = dataStore.getWallet()
            }
        } catch (t: Throwable) {
            println("[NumDoz/game] load failed: ${t.message}")
        } finally { loaded = true }
    }

    LaunchedEffect(loaded, isPaused, timeLeft) {
        if (!loaded || isPaused || showResult) return@LaunchedEffect
        if (timeLeft <= 0) {
            if (!showTimeoutModal) {
                showTimeoutModal = true
                audio.playSfx(Sfx.HINT)
                level?.let { lv ->
                    bgScope.launch {
                        try {
                            dataStore.upsertLevelProgress(
                                LevelProgress(
                                    levelId = lv.id, completed = false, bestTimeSeconds = 0,
                                    stars = 0, hintsUsed = hintsUsed, timeExtensionsUsed = timeExtUsed,
                                    lastPlayedAt = currentTimeMillis(), timeoutOccurred = true, timeLeftSeconds = 0
                                )
                            )
                        } catch (_: Throwable) { }
                    }
                }
            }
            return@LaunchedEffect
        }
        delay(1000)
        timeLeft--
    }

    LaunchedEffect(loaded) {
        if (!loaded) return@LaunchedEffect
        while (true) {
            delay(5000)
            val lv = level
            if (lv != null && !showResult && !showTimeoutModal && timeLeft > 0) {
                try {
                    dataStore.upsertLevelProgress(
                        LevelProgress(
                            levelId = lv.id, completed = false, bestTimeSeconds = 0,
                            stars = 0, hintsUsed = hintsUsed, timeExtensionsUsed = timeExtUsed,
                            lastPlayedAt = currentTimeMillis(), timeoutOccurred = false, timeLeftSeconds = timeLeft
                        )
                    )
                } catch (_: Throwable) { }
            }
        }
    }

    LaunchedEffect(wrongTrigger) {
        if (wrongTrigger == 0) return@LaunchedEffect
        shakeOffset.animateTo(0f, keyframes {
            durationMillis = 400
            -12f at 40; 12f at 100; -8f at 160; 8f at 220; -4f at 280; 4f at 340; 0f at 400
        })
    }

    fun saveAndBack() {
        val lv = level
        if (lv != null && !showResult) {
            val t = timeLeft.coerceAtLeast(0)
            bgScope.launch {
                try {
                    dataStore.upsertLevelProgress(
                        LevelProgress(
                            levelId = lv.id, completed = false, bestTimeSeconds = 0,
                            stars = 0, hintsUsed = hintsUsed, timeExtensionsUsed = timeExtUsed,
                            lastPlayedAt = currentTimeMillis(), timeoutOccurred = t == 0, timeLeftSeconds = t
                        )
                    )
                } catch (_: Throwable) { }
            }
        }
        onBack()
    }

    fun revealNextHint() {
        when {
            !hint1Revealed -> {
                hint1Revealed = true; hintsUsed++
                wallet = wallet.copy(freeHints = (wallet.freeHints - 1).coerceAtLeast(0))
                scope.launch { dataStore.updateWallet(wallet) }
                audio.playSfx(Sfx.HINT)
            }
            !hint2Revealed -> {
                hint2Revealed = true; hintsUsed++
                wallet = wallet.copy(freeHints = (wallet.freeHints - 1).coerceAtLeast(0))
                scope.launch { dataStore.updateWallet(wallet) }
                audio.playSfx(Sfx.HINT)
            }
        }
    }

    fun onHintIconClick() {
        audio.playSfx(Sfx.CLICK)
        if (!canRevealMore) return
        if (wallet.freeHints > 0) revealNextHint() else showHintModal = true
    }

    fun onTimeBoostClick() {
        audio.playSfx(Sfx.CLICK)
        if (wallet.freeTimeBoosts > 0) {
            wallet = wallet.copy(freeTimeBoosts = wallet.freeTimeBoosts - 1)
            timeLeft += 30; timeExtUsed++
            scope.launch { dataStore.updateWallet(wallet) }
            audio.playSfx(Sfx.CORRECT)
        } else {
            showCapsuleModal = true
        }
    }

    fun watchAdForHint() {
        adLoading = true; adError = null
        adManager.showRewarded(
            onRewarded = {
                wallet = wallet.copy(freeHints = wallet.freeHints + 1, totalAdsWatched = wallet.totalAdsWatched + 1)
                scope.launch { dataStore.updateWallet(wallet) }
                revealNextHint()
            },
            onClosed = { adLoading = false },
            onFailed = { msg -> adLoading = false; adError = msg }
        )
    }

    fun watchAdForCapsule() {
        adLoading = true; adError = null
        adManager.showRewarded(
            onRewarded = {
                wallet = wallet.copy(freeTimeBoosts = wallet.freeTimeBoosts + 1, totalAdsWatched = wallet.totalAdsWatched + 1)
                scope.launch { dataStore.updateWallet(wallet) }
            },
            onClosed = { adLoading = false },
            onFailed = { msg -> adLoading = false; adError = msg }
        )
    }

    fun watchAdForTime() {
        showTimeoutModal = false
        adLoading = true; adError = null
        adManager.showRewarded(
            onRewarded = { timeLeft = 60; timeExtUsed++ },
            onClosed = { adLoading = false },
            onFailed = { msg -> adLoading = false; adError = msg }
        )
    }

    fun useCapsuleFromTimeout() {
        if (wallet.freeTimeBoosts <= 0) { showCapsuleModal = true; return }
        wallet = wallet.copy(freeTimeBoosts = wallet.freeTimeBoosts - 1)
        timeLeft = 30; timeExtUsed++
        scope.launch { dataStore.updateWallet(wallet) }
        showTimeoutModal = false
        audio.playSfx(Sfx.CORRECT)
    }

    fun addStarViaAd() {
        val lv = level ?: return
        if (starsEarned >= 3) return
        starAdLoading = true; adError = null
        adManager.showRewarded(
            onRewarded = {
                starsEarned = (starsEarned + 1).coerceAtMost(3)
                wallet = wallet.copy(totalAdsWatched = wallet.totalAdsWatched + 1)
                scope.launch {
                    dataStore.updateWallet(wallet)
                    val existing = dataStore.getLevelProgress(lv.id)
                    if (existing != null) dataStore.upsertLevelProgress(existing.copy(stars = starsEarned))
                }
            },
            onClosed = { starAdLoading = false },
            onFailed = { msg -> starAdLoading = false; adError = msg }
        )
    }

    fun buyStarWithCoins() {
        val lv = level ?: return
        if (starsEarned >= 3) return
        if (wallet.coins < Economy.EXTRA_STAR_PRICE) {
            adError = strings.notEnoughCoins
            return
        }
        starBuyLoading = true
        wallet = wallet.copy(coins = wallet.coins - Economy.EXTRA_STAR_PRICE)
        scope.launch {
            dataStore.updateWallet(wallet)
            starsEarned = (starsEarned + 1).coerceAtMost(3)
            val existing = dataStore.getLevelProgress(lv.id)
            if (existing != null) dataStore.upsertLevelProgress(existing.copy(stars = starsEarned))
            starBuyLoading = false
        }
        audio.playSfx(Sfx.CORRECT)
    }

    fun skipLevelWithCoins() {
        val lv = level ?: return
        if (wallet.coins < Economy.SKIP_LEVEL_PRICE) {
            adError = strings.notEnoughCoins
            showSkipModal = false
            return
        }
        wallet = wallet.copy(coins = wallet.coins - Economy.SKIP_LEVEL_PRICE)
        scope.launch {
            dataStore.updateWallet(wallet)
            dataStore.upsertLevelProgress(
                LevelProgress(
                    levelId = lv.id, completed = true, bestTimeSeconds = 0,
                    stars = 0, hintsUsed = hintsUsed, timeExtensionsUsed = timeExtUsed,
                    lastPlayedAt = currentTimeMillis(),
                    timeoutOccurred = false, timeLeftSeconds = 0
                )
            )
        }
        showSkipModal = false
        audio.playSfx(Sfx.CORRECT)
        nextLevelId?.let { onNext(it) } ?: onBack()
    }

    fun skipLevelWithAd() {
        val lv = level ?: return
        showSkipModal = false
        adLoading = true; adError = null
        adManager.showRewarded(
            onRewarded = {
                scope.launch {
                    dataStore.upsertLevelProgress(
                        LevelProgress(
                            levelId = lv.id, completed = true, bestTimeSeconds = 0,
                            stars = 0, hintsUsed = hintsUsed, timeExtensionsUsed = timeExtUsed,
                            lastPlayedAt = currentTimeMillis(),
                            timeoutOccurred = false, timeLeftSeconds = 0
                        )
                    )
                    val w2 = dataStore.getWallet()
                    dataStore.updateWallet(w2.copy(totalAdsWatched = w2.totalAdsWatched + 1))
                }
            },
            onClosed = {
                adLoading = false
                nextLevelId?.let { onNext(it) } ?: onBack()
            },
            onFailed = { msg -> adLoading = false; adError = msg }
        )
    }

    fun goToNextLevel() {
        val nid = nextLevelId ?: return
        val isFirstSession = wallet.appSessions <= 1
        SessionTracker.levelsCompletedThisSession++
        if (!isFirstSession && SessionTracker.levelsCompletedThisSession % 3 == 0) {
            adLoading = true
            adManager.showInterstitial(
                onClosed = { adLoading = false; onNext(nid) },
                onFailed = { adLoading = false; onNext(nid) }
            )
        } else {
            onNext(nid)
        }
    }

    fun calculateStars(): Int {
        val total = 60 + timeExtUsed * 60
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
                        lastPlayedAt = currentTimeMillis(),
                        timeoutOccurred = false, timeLeftSeconds = timeLeft.coerceAtLeast(0)
                    )
                )
                val w2 = dataStore.getWallet()
                dataStore.updateWallet(w2.copy(coins = w2.coins + Economy.COINS_PER_LEVEL_COMPLETE))
            }
        } else {
            audio.playSfx(Sfx.WRONG)
            wrongTrigger++
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().widthIn(max = 460.dp)
                .align(Alignment.TopCenter).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { audio.playSfx(Sfx.CLICK); saveAndBack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onTimeBoostClick() }) {
                    BadgedBox(badge = {
                        if (wallet.freeTimeBoosts > 0) {
                            Badge(containerColor = capsuleColor, contentColor = Color.Black) {
                                Text("${wallet.freeTimeBoosts}")
                            }
                        }
                    }) {
                        Box(Modifier.size(24.dp)) {
                            TimeBoostIcon(
                                modifier = Modifier.fillMaxSize(),
                                tint = if (wallet.freeTimeBoosts > 0) capsuleColor else TextMuted,
                                animate = wallet.freeTimeBoosts > 0
                            )
                        }
                    }
                }
                Spacer(Modifier.size(4.dp))
                IconButton(enabled = canRevealMore, onClick = { onHintIconClick() }) {
                    BadgedBox(badge = {
                        if (wallet.freeHints > 0) {
                            Badge(containerColor = accent, contentColor = Color.White) {
                                Text("${wallet.freeHints}")
                            }
                        }
                    }) {
                        Box(Modifier.size(24.dp)) {
                            HintIcon(
                                modifier = Modifier.fillMaxSize(),
                                tint = when {
                                    !canRevealMore -> TextMuted
                                    wallet.freeHints > 0 -> accent
                                    else -> TextMuted
                                },
                                animate = wallet.freeHints > 0
                            )
                        }
                    }
                }
                Spacer(Modifier.size(4.dp))
                TimerChip(seconds = timeLeft, accent = accent)
            }
            Spacer(Modifier.height(6.dp))

            val subtitle = buildString {
                chapter?.displayTitle(lang)?.let { append(it) }
                section?.displayTitle(lang)?.let { append(" · ").append(it) }
                level?.dayNumber?.let { if (it > 0) append(" · ").append(strings.dayLabel(it)) }
            }
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted, modifier = Modifier.padding(start = 12.dp),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.weight(0.15f))
            level?.let { SequenceDisplay(sequence = it.sequence, accent = accent, label = strings.completeSequence) }
            Spacer(Modifier.height(24.dp))

            if (level != null) {
                val lv = level!!
                val keyboardType = if (lv.correctAnswer.all { c -> c.isDigit() }) KeyboardType.Number else KeyboardType.Text
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    value = input,
                    onValueChange = { if (it.length <= 12) input = it },
                    modifier = Modifier.fillMaxWidth()
                        .offset(x = shakeOffset.value.dp)
                        .focusRequester(focusRequester),
                    placeholder = { Text(strings.answerPlaceholder, color = TextMuted) },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center, color = accent, fontWeight = FontWeight.SemiBold
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent.copy(alpha = 0.7f),
                        unfocusedBorderColor = accent.copy(alpha = 0.25f),
                        cursorColor = accent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )
                LaunchedEffect(Unit) { delay(300); focusRequester.requestFocus() }
            }

            if (hint1Revealed) {
                Spacer(Modifier.height(12.dp))
                HintCard(text = level?.displayHint1(lang) ?: "", accent = accent)
            }
            if (hint2Revealed) {
                Spacer(Modifier.height(8.dp))
                HintCard(text = level?.displayHint2(lang) ?: "", accent = accent)
            }

            Spacer(Modifier.height(20.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.88f), accent.copy(alpha = 0.62f))))
                    .clickable { submit() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Text(strings.submit, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            if (!showResult) {
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = { audio.playSfx(Sfx.CLICK); showSkipModal = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.ChevronRight, null, tint = skipColor, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(
                        if (lang == Lang.FA) "این مرحله سخته؟ رد شو" else "Stuck? Skip it",
                        color = skipColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.weight(0.3f))
        }

        if (showHintModal) {
            ModalOverlay(onDismiss = { showHintModal = false }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(28.dp)) { HintIcon(modifier = Modifier.fillMaxSize(), tint = accent, animate = true) }
                        Text(strings.youHaveNoHintsTitle, style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                    Text(strings.youHaveNoHintsDesc, style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary, textAlign = TextAlign.Center, maxLines = 2)
                    PrimaryActionButton(strings.watchAdForHint, accent) {
                        showHintModal = false
                        watchAdForHint()
                    }
                    TextButton(onClick = { audio.playSfx(Sfx.CLICK); showHintModal = false }) {
                        Text(strings.close, color = TextSecondary)
                    }
                }
            }
        }

        if (showCapsuleModal) {
            ModalOverlay(onDismiss = { showCapsuleModal = false }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(32.dp)) { TimeBoostIcon(modifier = Modifier.fillMaxSize(), tint = capsuleColor, animate = true) }
                        Text(if (lang == Lang.FA) "کپسول زمان" else "Time Capsule",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                    Text(
                        if (lang == Lang.FA) "با تماشای تبلیغ یک کپسول رایگان بگیر" else "Watch an ad to get a free capsule",
                        style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                        textAlign = TextAlign.Center, maxLines = 2
                    )
                    PrimaryActionButton(
                        if (lang == Lang.FA) "تماشای تبلیغ — دریافت کپسول" else "Watch ad — get capsule",
                        capsuleColor
                    ) {
                        showCapsuleModal = false
                        watchAdForCapsule()
                    }
                    TextButton(onClick = { audio.playSfx(Sfx.CLICK); showCapsuleModal = false }) {
                        Text(strings.close, color = TextSecondary)
                    }
                }
            }
        }

        if (showSkipModal) {
            SkipLevelModal(
                coins = wallet.coins,
                accent = skipColor,
                onSkipWithCoins = { skipLevelWithCoins() },
                onSkipWithAd = { skipLevelWithAd() },
                onClose = { audio.playSfx(Sfx.CLICK); showSkipModal = false }
            )
        }

        AnimatedVisibility(
            visible = showResult,
            enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.85f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        ) {
            ResultOverlay(
                stars = starsEarned, elapsed = elapsed, hintsUsed = hintsUsed,
                accent = accent, hasNext = nextLevelId != null,
                canGetMoreStars = starsEarned < 3,
                starAdLoading = starAdLoading,
                coins = wallet.coins,
                starBuyLoading = starBuyLoading,
                onWatchAdForStar = { addStarViaAd() },
                onBuyStarWithCoins = { buyStarWithCoins() },
                onNext = { audio.playSfx(Sfx.CLICK); goToNextLevel() },
                onBack = { audio.playSfx(Sfx.CLICK); onBack() }
            )
        }

        if (showTimeoutModal) {
            ModalOverlay(onDismiss = { saveAndBack() }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(strings.timesUp, style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Text(
                        if (lang == Lang.FA) "برای ادامه یکی از گزینه‌های زیر را انتخاب کنید:" else "Choose an option:",
                        style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                        textAlign = TextAlign.Center, maxLines = 2
                    )
                    if (wallet.freeTimeBoosts > 0) {
                        Row(
                            Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(capsuleColor.copy(alpha = 0.88f), capsuleColor.copy(alpha = 0.62f))))
                                .clickable { useCapsuleFromTimeout() }.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(Modifier.size(22.dp)) { TimeBoostIcon(modifier = Modifier.fillMaxSize(), tint = Color.White, animate = false) }
                            Spacer(Modifier.size(8.dp))
                            Text(
                                if (lang == Lang.FA) "استفاده از کپسول (۳۰s · ${wallet.freeTimeBoosts})" else "Use capsule (30s · ${wallet.freeTimeBoosts})",
                                color = Color.White, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold, maxLines = 1
                            )
                        }
                    }
                    PrimaryActionButton(strings.watchAd, accent) { watchAdForTime() }
                    Box(
                        Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(skipColor.copy(alpha = 0.22f), skipColor.copy(alpha = 0.08f))))
                            .clickable { showTimeoutModal = false; showSkipModal = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.ChevronRight, null, tint = skipColor, modifier = Modifier.size(22.dp))
                            Text(
                                if (lang == Lang.FA) "پرش از مرحله" else "Skip level",
                                color = skipColor, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold, maxLines = 1
                            )
                        }
                    }
                    TextButton(onClick = { audio.playSfx(Sfx.CLICK); saveAndBack() }) {
                        Text(strings.backWithoutContinue, color = TextMuted)
                    }
                }
            }
        }

        if (adLoading) {
            BlockingOverlay {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator(color = accent, strokeWidth = 3.dp)
                    Text(
                        if (lang == Lang.FA) "در حال بارگذاری تبلیغ…" else "Loading ad…",
                        style = MaterialTheme.typography.titleMedium, color = TextSecondary
                    )
                }
            }
        }

        adError?.let { err ->
            BlockingOverlay {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        if (lang == Lang.FA) "تبلیغ در دسترس نیست" else "Ad not available",
                        style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1
                    )
                    Text(err, style = MaterialTheme.typography.labelSmall, color = TextMuted, textAlign = TextAlign.Center, maxLines = 3)
                    PrimaryActionButton(if (lang == Lang.FA) "بستن" else "Close", accent) { adError = null }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// Skip Level modal — NEW
// ═══════════════════════════════════════════════════════════════

@Composable
private fun SkipLevelModal(
    coins: Int,
    accent: Color,
    onSkipWithCoins: () -> Unit,
    onSkipWithAd: () -> Unit,
    onClose: () -> Unit
) {
    val isFa = LocalLang.current == Lang.FA
    val strings = LocalStrings.current
    val canAfford = coins >= Economy.SKIP_LEVEL_PRICE

    ModalOverlay(onDismiss = onClose) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier.size(64.dp).clip(RoundedCornerShape(20.dp))
                    .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.25f), accent.copy(alpha = 0.06f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ChevronRight, null, tint = accent, modifier = Modifier.size(36.dp))
            }
            Text(
                if (isFa) "پرش از مرحله" else "Skip Level",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 1
            )
            Text(
                if (isFa) "این مرحله بدون ستاره رد می‌شه و مرحله بعد باز می‌شه."
                else "This level is skipped without stars. Next level unlocks.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary, textAlign = TextAlign.Center, maxLines = 2
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallPill(text = if (isFa) "${coins} سکه" else "$coins coins", accent = Color(0xFFFFC947))
            }

            if (canAfford) {
                Row(
                    Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFFFC947).copy(alpha = 0.88f), Color(0xFFFFC947).copy(alpha = 0.62f))))
                        .clickable(onClick = onSkipWithCoins).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(Modifier.size(22.dp)) { CoinIcon(modifier = Modifier.fillMaxSize(), tint = Color.White, animate = false) }
                    Spacer(Modifier.size(8.dp))
                    Text(
                        if (isFa) "پرش با ${Economy.SKIP_LEVEL_PRICE} سکه" else "Skip for ${Economy.SKIP_LEVEL_PRICE} coins",
                        color = Color.White, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold, maxLines = 1
                    )
                }
            } else {
                Text(
                    if (isFa) "سکه کافی نداری — از تبلیغ استفاده کن" else "Not enough coins — use ad instead",
                    style = MaterialTheme.typography.bodySmall, color = Color(0xFFF87171),
                    textAlign = TextAlign.Center, maxLines = 2
                )
            }

            Row(
                Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.22f), accent.copy(alpha = 0.08f))))
                    .clickable(onClick = onSkipWithAd).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Videocam, null, tint = accent, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(8.dp))
                Text(
                    if (isFa) "پرش با تماشای تبلیغ" else "Skip via ad",
                    color = accent, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, maxLines = 1
                )
            }

            TextButton(onClick = onClose) {
                Text(strings.close, color = TextSecondary)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// Overlays
// ═══════════════════════════════════════════════════════════════

@Composable
private fun BlockingOverlay(content: @Composable () -> Unit) {
    Box(
        Modifier.fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xE808080E), Color(0xF408080E))))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun ModalOverlay(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier.fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xE808080E), Color(0xF408080E))))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.widthIn(max = 380.dp).padding(24.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1E1E2E), Color(0xFF14141D))))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) { content() }
    }
}

// ═══════════════════════════════════════════════════════════════
// Result Overlay with star buy option
// ═══════════════════════════════════════════════════════════════

@Composable
private fun ResultOverlay(
    stars: Int, elapsed: Int, hintsUsed: Int, accent: Color,
    hasNext: Boolean, canGetMoreStars: Boolean, starAdLoading: Boolean,
    coins: Int, starBuyLoading: Boolean,
    onWatchAdForStar: () -> Unit,
    onBuyStarWithCoins: () -> Unit,
    onNext: () -> Unit, onBack: () -> Unit
) {
    val isFa = LocalLang.current == Lang.FA
    val strings = LocalStrings.current

    val title: String
    val subtitle: String
    when (stars) {
        3 -> {
            title = if (isFa) "عالی! استاد شدی" else "Perfect! You mastered it"
            subtitle = if (isFa) "بدون هیچ راهنمایی حلش کردی" else "Solved with no hints"
        }
        2 -> {
            title = if (isFa) "آفرین! نزدیک به کامل" else "Great! Almost perfect"
            subtitle = if (isFa) "با یک راهنما هم می‌شه سه‌ستاره گرفت" else "Try for 3 stars next time"
        }
        else -> {
            title = if (isFa) "درست بود!" else "Correct!"
            subtitle = if (isFa) "کمتر راهنما بگیر، بیشتر ستاره بگیر" else "Fewer hints = more stars"
        }
    }

    val goldenStar = Color(0xFFFFC947)
    val starColor = if (stars >= 2) goldenStar else accent

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Box(
            Modifier.fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xE808080E), Color(0xF808080E))))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            if (stars >= 2) {
                ConfettiLayer(accent = starColor, intensity = if (stars == 3) 1f else 0.6f)
            }
            Column(
                Modifier.widthIn(max = 380.dp).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.fillMaxWidth().height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PulsingHalo(accent = starColor, stars = stars)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(3) { i ->
                            AnimatedStar(
                                filled = i < stars,
                                accent = starColor,
                                delayMs = 150 + i * 220
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = starColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val mm = elapsed / 60
                    val ss = elapsed % 60
                    SmallPill(text = "${mm}:${ss.toString().padStart(2, '0')}", accent = accent)
                    if (hintsUsed > 0) {
                        SmallPill(
                            text = if (isFa) "$hintsUsed راهنما" else "$hintsUsed hints",
                            accent = Color(0xFFFBBF24)
                        )
                    } else {
                        SmallPill(
                            text = if (isFa) "بدون راهنما" else "No hints",
                            accent = Color(0xFF34D399)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (canGetMoreStars) {
                    StarRewardButton(
                        loading = starAdLoading,
                        accent = goldenStar,
                        label = if (isFa) "۱ ستاره با تبلیغ" else "1 star via ad",
                        onClick = onWatchAdForStar
                    )
                    if (coins >= Economy.EXTRA_STAR_PRICE) {
                        Spacer(Modifier.height(6.dp))
                        Row(
                            Modifier.fillMaxWidth().height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(goldenStar.copy(alpha = 0.14f), goldenStar.copy(alpha = 0.05f))))
                                .clickable(enabled = !starBuyLoading && !starAdLoading, onClick = onBuyStarWithCoins)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (starBuyLoading) {
                                CircularProgressIndicator(color = goldenStar, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            } else {
                                Box(Modifier.size(18.dp)) { CoinIcon(modifier = Modifier.fillMaxSize(), tint = goldenStar, animate = false) }
                            }
                            Spacer(Modifier.size(6.dp))
                            Text(
                                if (isFa) "یا خرید ستاره با ${Economy.EXTRA_STAR_PRICE} سکه" else "or buy star for ${Economy.EXTRA_STAR_PRICE} coins",
                                color = goldenStar,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                if (hasNext) {
                    PrimaryActionButton(strings.nextLevel, accent, onNext)
                    Spacer(Modifier.height(6.dp))
                }
                TextButton(onClick = onBack) {
                    Text(strings.backToList, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun PulsingHalo(accent: Color, stars: Int) {
    val t = rememberInfiniteTransition(label = "halo")
    val pulse by t.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "p"
    )
    val intensity = when (stars) { 3 -> 0.45f; 2 -> 0.30f; else -> 0.18f }
    Canvas(Modifier.fillMaxSize()) {
        val r = size.minDimension * 0.65f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = intensity * pulse), Color.Transparent),
                center = center, radius = r
            ),
            radius = r, center = center
        )
    }
}

@Composable
private fun AnimatedStar(filled: Boolean, accent: Color, delayMs: Int) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(-90f) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        launch { rotation.animateTo(0f, tween(450, easing = FastOutSlowInEasing)) }
        scale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = null,
        tint = if (filled) accent else accent.copy(alpha = 0.15f),
        modifier = Modifier
            .size(52.dp)
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value,
                rotationZ = rotation.value
            )
    )
}

private data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val sway: Float
)

@Composable
private fun ConfettiLayer(accent: Color, intensity: Float) {
    val particles = remember {
        val count = if (intensity > 0.8f) 28 else 16
        List(count) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.6f,
                speed = 0.5f + Random.nextFloat() * 0.7f,
                size = 2.5f + Random.nextFloat() * 4f,
                color = when (Random.nextInt(4)) {
                    0 -> accent
                    1 -> Color(0xFFFFC947)
                    2 -> Color(0xFF22D3EE)
                    else -> Color(0xFFF472B6)
                },
                sway = (Random.nextFloat() - 0.5f) * 20f
            )
        }
    }
    val t = rememberInfiniteTransition(label = "confetti")
    val progress by t.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(4200, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "p"
    )
    Canvas(Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val yFrac = ((p.startY + progress * p.speed) % 1f + 1f) % 1f
            val xPos = p.x * size.width + p.sway * kotlin.math.sin(progress * 6.28f + p.x * 5f)
            drawCircle(
                color = p.color.copy(alpha = 0.75f),
                radius = p.size,
                center = Offset(xPos, yFrac * size.height)
            )
        }
    }
}

@Composable
private fun SmallPill(text: String, accent: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.18f), accent.copy(alpha = 0.08f))))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = accent,
            fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun StarRewardButton(
    loading: Boolean, accent: Color, label: String, onClick: () -> Unit
) {
    val t = rememberInfiniteTransition(label = "shimmer")
    val shimmer by t.animateFloat(
        initialValue = -1f, targetValue = 2f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "s"
    )
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                drawRect(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.25f), accent.copy(alpha = 0.10f))))
                val shimX = shimmer * size.width
                drawRect(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, accent.copy(alpha = 0.35f), Color.Transparent),
                        startX = shimX - size.width * 0.3f,
                        endX = shimX + size.width * 0.3f
                    )
                )
            }
            .clickable(enabled = !loading, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (loading) {
            CircularProgressIndicator(color = accent, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Box(Modifier.size(22.dp)) { StarRewardIcon(modifier = Modifier.fillMaxSize(), tint = accent) }
        }
        Spacer(Modifier.size(10.dp))
        Text(label, color = accent, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

// ═══════════════════════════════════════════════════════════════
// Basic helpers
// ═══════════════════════════════════════════════════════════════

@Composable
private fun TimerChip(seconds: Int, accent: Color) {
    val danger = seconds <= 15
    val c = if (danger) Color(0xFFF87171) else accent
    val mm = seconds / 60; val ss = seconds % 60
    Box(
        Modifier.clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(c.copy(alpha = 0.18f), c.copy(alpha = 0.08f))))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text("${mm}:${ss.toString().padStart(2, '0')}", color = c,
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SequenceDisplay(sequence: String, accent: Color, label: String) {
    val parts = sequence.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            parts.forEachIndexed { i, p ->
                key(i) {
                    val isQ = p == "?"
                    val isLast = i == parts.lastIndex
                    Box(
                        Modifier.size(if (p.length > 2) 52.dp else 42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isQ) Brush.horizontalGradient(listOf(accent.copy(alpha = 0.15f), accent.copy(alpha = 0.05f)))
                                else Brush.horizontalGradient(listOf(accent.copy(alpha = 0.22f), accent.copy(alpha = 0.10f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(p, color = if (isQ) accent else TextPrimary,
                            style = if (isQ) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                            fontWeight = if (isLast) FontWeight.SemiBold else FontWeight.Normal)
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
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.14f), accent.copy(alpha = 0.06f))))
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(22.dp)) { HintIcon(modifier = Modifier.fillMaxSize(), tint = accent, animate = false) }
            Text(text = text, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PrimaryActionButton(text: String, accent: Color, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.88f), accent.copy(alpha = 0.62f))))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

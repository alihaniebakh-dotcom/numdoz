package ir.numdoz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ir.numdoz.AppInfo
import ir.numdoz.ads.LocalAdManager
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.UserSettings
import ir.numdoz.data.model.UserWallet
import ir.numdoz.data.model.Year
import ir.numdoz.i18n.Lang
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.LocalStrings
import ir.numdoz.i18n.displayTitle
import ir.numdoz.ui.components.AnimatedLogo
import ir.numdoz.ui.components.YearCard
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun YearListScreen(
    dataStore: DataStore,
    currentLang: Lang,
    onLanguageChange: (Lang) -> Unit,
    onTintChange: (Color?) -> Unit,
    onYearClick: (String) -> Unit
) {
    val audio = LocalAudio.current
    val adManager = LocalAdManager.current
    val accent = LocalAccent.current
    val strings = LocalStrings.current
    val lang = LocalLang.current
    val scope = rememberCoroutineScope()

    var ready by remember { mutableStateOf(false) }
    var years by remember { mutableStateOf<List<Year>>(emptyList()) }
    var wallet by remember { mutableStateOf(UserWallet()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var pendingYear by remember { mutableStateOf<Year?>(null) }

    var showHintModal by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }
    var adLoading by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }
    

    LaunchedEffect(Unit) {
        onTintChange(null)
        delay(60)
        ready = true
        try {
            dataStore.ensureSchema()
            dataStore.seedIfEmpty()
            years = dataStore.getAllYears()
            wallet = dataStore.getWallet()
            println("[NumDoz/ui] YearList loaded ${years.size} years, hints=${wallet.freeHints}")
        } catch (t: Throwable) {
            println("[NumDoz/ui] YearList error: ${t.message}")
            error = t.message ?: t.javaClass.simpleName
        } finally { loading = false }
    }

    

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 460.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            AnimatedVisibility(
                visible = ready,
                enter = fadeIn(tween(800)) +
                        slideInVertically(
                            initialOffsetY = { -30 },
                            animationSpec = tween(900)
                        )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedLogo(size = 110.dp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "NUMDOZ",
                        style = MaterialTheme.typography.displayLarge.copy(letterSpacing = 9.sp),
                        fontWeight = FontWeight.Light,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = strings.tagline,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            AnimatedVisibility(visible = ready, enter = fadeIn(tween(600, delayMillis = 420))) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 44.dp)
                        .height(1.dp)
                        .background(Color(0x33FFFFFF))
                )
            }

            Spacer(Modifier.height(36.dp))

            AnimatedVisibility(visible = ready, enter = fadeIn(tween(500, delayMillis = 500))) {
                Text(
                    text = strings.yearsLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(12.dp))

            when {
                loading -> Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accent, strokeWidth = 2.dp)
                }

                error != null -> Text(
                    text = "Error: $error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFF87171),
                    textAlign = TextAlign.Center
                )

                else -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    years.forEachIndexed { idx, y ->
                        val yColor = parseHex(y.accentColorHex) ?: accent
                        AnimatedVisibility(
                            visible = ready,
                            enter = fadeIn(tween(600, delayMillis = (600 + idx * 150))) +
                                    slideInVertically(
                                        initialOffsetY = { 24 },
                                        animationSpec = tween(600, delayMillis = 600 + idx * 150)
                                    )
                        ) {
                            YearCard(
                                title = y.displayTitle(lang),
                                titleEn = if (lang == Lang.FA) y.titleEn else y.titleFa,
                                accent = yColor,
                                logoName = y.logoName,
                                locked = y.isLocked,
                                comingSoonLabel = strings.comingSoon,
                                onClick = {
                                    audio.playSfx(Sfx.CLICK)
                                    scope.launch {
                                        if (!dataStore.isIntroSeen("year_${y.id}")) {
                                            pendingYear = y
                                        } else {
                                            onYearClick(y.id)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(10f)
                .padding(top = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HomeHintChip(
                count = wallet.freeHints,
                accent = accent,
                onClick = {
                    audio.playSfx(Sfx.CLICK)
                    showSettingsModal = false
                    showHintModal = true
                }
            )
            Spacer(Modifier.size(2.dp))
            IconButton(onClick = {
                audio.playSfx(Sfx.CLICK)
                showHintModal = false
                showSettingsModal = true
            }) {
                Icon(Icons.Outlined.Settings, "Settings", tint = TextSecondary)
            }
            IconButton(onClick = {
                audio.playSfx(Sfx.CLICK)
                audio.toggleMute()
            }) {
                Icon(
                    imageVector = if (audio.isMuted) Icons.AutoMirrored.Outlined.VolumeOff
                                  else Icons.AutoMirrored.Outlined.VolumeUp,
                    contentDescription = if (audio.isMuted) "Unmute" else "Mute",
                    tint = if (audio.isMuted) TextMuted else TextSecondary
                )
            }
        }

        if (showHintModal) {
            HomeHintModal(
                wallet = wallet,
                accent = accent,
                onWatchAd = {
                    showHintModal = false
                    adLoading = true
                    adError = null
                    adManager.showRewarded(
                        onRewarded = {
                            wallet = wallet.copy(
                                freeHints = wallet.freeHints + 1,
                                totalAdsWatched = wallet.totalAdsWatched + 1
                            )
                            scope.launch { dataStore.updateWallet(wallet) }
                        },
                        onClosed = { adLoading = false },
                        onFailed = { msg -> adLoading = false; adError = msg }
                    )
                },
                onBuy = {},
                onClose = {
                    audio.playSfx(Sfx.CLICK)
                    showHintModal = false
                }
            )
        }

        if (showSettingsModal) {
            HomeSettingsModal(
                currentLang = currentLang,
                accent = accent,
                isMuted = audio.isMuted,
                onToggleSound = { audio.toggleMute() },
                onToggleLanguage = {
                    val newLang = if (currentLang == Lang.FA) Lang.EN else Lang.FA
                    onLanguageChange(newLang)
                    scope.launch {
                        val current = dataStore.getSettings()
                        dataStore.updateSettings(current.copy(
                            language = if (newLang == Lang.FA) "fa" else "en"
                        ))
                    }
                },
                onClose = {
                    audio.playSfx(Sfx.CLICK)
                    showSettingsModal = false
                }
            )
        }

        if (adLoading) {
            AdLoadingOverlay(accent = accent)
        }

        adError?.let { err ->
            AdErrorModal(
                errorMessage = err,
                accent = accent,
                onRetry = { adError = null; adLoading = true; adManager.showRewarded(
                    onRewarded = {
                        wallet = wallet.copy(
                            freeHints = wallet.freeHints + 1,
                            totalAdsWatched = wallet.totalAdsWatched + 1
                        )
                        scope.launch { dataStore.updateWallet(wallet) }
                    },
                    onClosed = { adLoading = false },
                    onFailed = { msg -> adLoading = false; adError = msg }
                ) },
                onClose = { adError = null }
            )
        }

        pendingYear?.let { y ->
            IntroDialog(
                logoName = y.logoName,
                accent = parseHex(y.accentColorHex) ?: accent,
                title = y.displayTitle(lang),
                subtitle = if (lang == Lang.FA) y.titleEn else y.titleFa,
                description = if (lang == Lang.FA) y.descriptionFa else y.descriptionEn,
                buttonLabel = strings.enterYear,
                onDismiss = {
                    val id = y.id
                    pendingYear = null
                    scope.launch {
                        dataStore.markIntroSeen("year_$id")
                        onYearClick(id)
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeHintChip(count: Int, accent: Color, onClick: () -> Unit) {
    val strings = LocalStrings.current
    val hasHints = count > 0
    val fg = if (hasHints) accent else TextMuted
    val bgAlpha = if (hasHints) 0.15f else 0.06f
    val borderAlpha = if (hasHints) 0.5f else 0.2f

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(fg.copy(alpha = bgAlpha))
            .border(1.dp, fg.copy(alpha = borderAlpha), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (hasHints) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (hasHints) strings.hintWithCount(count) else strings.noHintsLabel,
            style = MaterialTheme.typography.labelLarge,
            color = fg,
            fontWeight = if (hasHints) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun HomeHintModal(
    wallet: UserWallet,
    accent: Color,
    onWatchAd: () -> Unit,
    onBuy: () -> Unit,
    onClose: () -> Unit
) {
    val strings = LocalStrings.current
    val hasHints = wallet.freeHints > 0
    ModalScaffold(accent = accent) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(accent.copy(alpha = 0.12f))
                .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (hasHints) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = strings.hintLabel,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(accent.copy(alpha = 0.10f))
                .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (hasHints) strings.youHaveNHints(wallet.freeHints) else strings.noHintsLabel,
                style = MaterialTheme.typography.titleMedium,
                color = accent
            )
        }
        Text(
            text = if (hasHints) strings.hintModalDesc else strings.hintModalDescEmpty,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        ModalActionButton(
            text = strings.watchAdForHint,
            icon = { Icon(Icons.Outlined.Videocam, null, tint = Color.White, modifier = Modifier.size(20.dp)) },
            accent = accent,
            primary = true,
            onClick = onWatchAd
        )
        ModalActionButton(
            text = strings.buyHintComing,
            icon = { Icon(Icons.Outlined.ShoppingCart, null, tint = TextMuted, modifier = Modifier.size(20.dp)) },
            accent = accent,
            primary = false,
            onClick = onBuy
        )
        TextButton(onClick = onClose) {
            Text(strings.close, color = TextSecondary)
        }
    }
}

@Composable
private fun HomeSettingsModal(
    currentLang: Lang,
    accent: Color,
    isMuted: Boolean,
    onToggleSound: () -> Unit,
    onToggleLanguage: () -> Unit,
    onClose: () -> Unit
) {
    val strings = LocalStrings.current
    ModalScaffold(accent = accent) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(accent.copy(alpha = 0.12f))
                .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Settings, null, tint = accent, modifier = Modifier.size(36.dp))
        }
        Text(
            text = strings.settingsTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        SettingsRow(
            icon = { Icon(Icons.Outlined.Language, null, tint = accent, modifier = Modifier.size(20.dp)) },
            label = strings.languageLabel,
            value = if (currentLang == Lang.FA) strings.languageFa else strings.languageEn,
            onClick = onToggleLanguage,
            muted = false
        )
        SettingsRow(
            icon = {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Outlined.VolumeOff
                                  else Icons.AutoMirrored.Outlined.VolumeUp,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            },
            label = strings.soundLabel,
            value = if (isMuted) strings.soundOff else strings.soundOn,
            onClick = onToggleSound
        )
        SettingsRow(
            icon = { Icon(Icons.Outlined.RestartAlt, null, tint = TextMuted, modifier = Modifier.size(20.dp)) },
            label = strings.resetProgressLabel,
            value = strings.comingSoonShort,
            muted = true
        )
        SettingsRow(
            icon = { Icon(Icons.Outlined.Info, null, tint = accent, modifier = Modifier.size(20.dp)) },
            label = strings.aboutLabel,
            value = "NumDoz v${AppInfo.VERSION_NAME}",
            muted = false
        )
        TextButton(onClick = onClose) {
            Text(strings.close, color = TextSecondary)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
    muted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A28))
            .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(12.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        icon()
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (muted) TextMuted else TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (muted) TextMuted else TextSecondary
        )
    }
}

@Composable
private fun HomeAdOverlay(adSeconds: Int, accent: Color) {
    val strings = LocalStrings.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE608080E))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF12121D))
                .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(strings.ad, style = MaterialTheme.typography.labelLarge, color = TextMuted)
            Text(
                text = strings.countdown(adSeconds),
                style = MaterialTheme.typography.displayMedium,
                color = accent,
                fontWeight = FontWeight.Light
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.06f))
                    .border(1.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(strings.ad, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = strings.adRewardMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ModalScaffold(accent: Color, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC08080E))
            .zIndex(15f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF12121D))
                .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ModalActionButton(
    text: String,
    icon: @Composable () -> Unit,
    accent: Color,
    primary: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (primary) accent.copy(alpha = 0.85f) else Color(0xFF1A1A28))
            .border(
                1.dp,
                if (primary) Color.Transparent else Color(0x22FFFFFF),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(Modifier.size(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (primary) Color.White else TextMuted,
            fontWeight = if (primary) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

private fun parseHex(hex: String): Color? = try {
    val h = hex.removePrefix("#")
    if (h.length == 6) Color(0xFF000000 or h.toLong(16)) else null
} catch (_: Throwable) { null }

@Composable
private fun AdLoadingOverlay(accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC08080E))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF12121D))
                .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = accent, strokeWidth = 3.dp)
            Text(
                text = "در حال بارگذاری تبلیغ…",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun AdErrorModal(
    errorMessage: String,
    accent: Color,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC08080E))
            .zIndex(20f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF12121D))
                .border(1.dp, Color(0xFFF87171).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "تبلیغ در دسترس نیست",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "لطفاً اتصال اینترنت را بررسی کنید یا لحظه‌ای بعد دوباره تلاش کنید.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1A28))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Text("بستن", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.85f))
                        .clickable(onClick = onRetry),
                    contentAlignment = Alignment.Center
                ) {
                    Text("تلاش دوباره", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

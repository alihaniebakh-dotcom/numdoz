package ir.numdoz.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.numdoz.ads.LocalAdManager
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.Sfx
import ir.numdoz.data.local.DataStore
import ir.numdoz.data.model.UserProfile
import ir.numdoz.data.model.UserStats
import ir.numdoz.data.model.UserWallet
import ir.numdoz.economy.Economy
import ir.numdoz.i18n.Lang
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.toFaNum
import ir.numdoz.profile.AVATARS
import ir.numdoz.profile.AvatarSpec
import ir.numdoz.profile.FaceAvatar
import ir.numdoz.profile.Titles
import ir.numdoz.profile.avatarById
import ir.numdoz.ui.components.CoinIcon
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════
// Main Screen
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun ProfileScreen(
    dataStore: DataStore,
    onBack: () -> Unit,
    onShopClick: () -> Unit = {}
) {
    val audio = LocalAudio.current
    val adManager = LocalAdManager.current
    val lang = LocalLang.current
    val isFa = lang == Lang.FA
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    var profile by remember { mutableStateOf(UserProfile()) }
    var stats by remember { mutableStateOf(UserStats()) }
    var wallet by remember { mutableStateOf(UserWallet()) }
    var editing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editAvatarId by remember { mutableIntStateOf(0) }
    var contentVisible by remember { mutableStateOf(false) }
    var statsVisible by remember { mutableStateOf(false) }
    var showEditGateModal by remember { mutableStateOf(false) }
    var adLoading by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            profile = dataStore.getProfile()
            stats = dataStore.getUserStats()
            wallet = dataStore.getWallet()
            editName = profile.username
            editAvatarId = profile.avatarId
        } catch (_: Throwable) { }
        loading = false
        delay(80)
        contentVisible = true
        delay(400)
        statsVisible = true
    }

    val av = avatarById(profile.avatarId)
    val title = Titles.current(stats.levelsCompleted)
    val nextTitle = Titles.next(stats.levelsCompleted)
    val progress = Titles.progressToNext(stats.levelsCompleted)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress, contentVisible) {
        if (contentVisible) {
            animatedProgress.animateTo(progress, tween(1000, easing = FastOutSlowInEasing))
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Box(Modifier.fillMaxSize()) {

            // Ambient glow
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(av.accentColor.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height * 0.22f),
                        radius = size.width * 1.1f
                    ),
                    radius = size.width * 1.1f,
                    center = Offset(size.width / 2f, size.height * 0.22f)
                )
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .widthIn(max = 460.dp)
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { audio.playSfx(Sfx.CLICK); onBack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = TextSecondary)
                    }
                    Spacer(Modifier.weight(1f))
                }

                if (loading) {
                    Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = av.accentColor)
                    }
                } else if (!editing) {
                    // ─────────── VIEW MODE ───────────
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(tween(600)) +
                                scaleIn(initialScale = 0.7f, animationSpec = tween(700, easing = FastOutSlowInEasing))
                    ) {
                        Box(
                            Modifier.fillMaxWidth().height(190.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AvatarHalo(accentColor = av.accentColor)
                            Box(Modifier.size(140.dp)) {
                                FaceAvatar(spec = av, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(tween(500, delayMillis = 250)) +
                                slideInVertically(initialOffsetY = { 15 }, animationSpec = tween(500, delayMillis = 250))
                    ) {
                        Text(
                            text = profile.username.ifBlank { "—" },
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(tween(500, delayMillis = 400))
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            GlowingTitleBadge(
                                icon = title.icon,
                                label = Titles.nameFor(title, lang),
                                color = title.color
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    if (nextTitle != null) {
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(tween(500, delayMillis = 550))
                        ) {
                            Column {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (isFa) "تا لقب «${Titles.nameFor(nextTitle, lang)}»"
                                               else "To ${Titles.nameFor(nextTitle, lang)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        if (isFa) "${toFaNum(stats.levelsCompleted)} / ${toFaNum(nextTitle.minLevels)}"
                                               else "${stats.levelsCompleted} / ${nextTitle.minLevels}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = title.color,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { animatedProgress.value },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = title.color,
                                    trackColor = Color(0xFF2A2A3E)
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    AnimatedVisibility(
                        visible = statsVisible,
                        enter = fadeIn(tween(500)) +
                                slideInVertically(initialOffsetY = { 25 }, animationSpec = tween(500))
                    ) {
                        Column {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    value = if (isFa) toFaNum(stats.levelsCompleted) else "${stats.levelsCompleted}",
                                    label = if (isFa) "مرحله" else "Levels",
                                    accent = av.accentColor,
                                    icon = { Icon(Icons.Outlined.Check, null, tint = av.accentColor, modifier = Modifier.size(16.dp)) }
                                )
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    value = if (isFa) toFaNum(stats.totalStars) else "${stats.totalStars}",
                                    label = if (isFa) "ستاره" else "Stars",
                                    accent = Color(0xFFFFC947),
                                    icon = { Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC947), modifier = Modifier.size(16.dp)) }
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    value = if (isFa) toFaNum(stats.hintsUsed) else "${stats.hintsUsed}",
                                    label = if (isFa) "راهنما" else "Hints",
                                    accent = Color(0xFFFBBF24),
                                    icon = { Icon(Icons.Outlined.Lightbulb, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp)) }
                                )
                                StatCard(
                                    modifier = Modifier.weight(1f),
                                    value = if (isFa) "${toFaNum(stats.daysSinceJoined.toInt())} روز" else "${stats.daysSinceJoined}d",
                                    label = if (isFa) "از عضویت" else "Joined",
                                    accent = Color(0xFF34D399),
                                    icon = { Icon(Icons.Outlined.PlayArrow, null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = statsVisible,
                        enter = fadeIn(tween(500, delayMillis = 300))
                    ) {
                        Box(
                            Modifier.fillMaxWidth().height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(av.accentColor.copy(alpha = 0.22f), av.accentColor.copy(alpha = 0.08f))
                                    )
                                )
                                .clickable {
                                    audio.playSfx(Sfx.CLICK)
                                    if (profile.editCount == 0) {
                                        editName = profile.username
                                        editAvatarId = profile.avatarId
                                        editing = true
                                    } else {
                                        showEditGateModal = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isFa) "ویرایش پروفایل" else "Edit profile",
                                color = av.accentColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(40.dp))
                } else {
                    // ─────────── EDIT MODE ───────────
                    val editAv = avatarById(editAvatarId)

                    Box(
                        Modifier.fillMaxWidth().height(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AvatarHalo(accentColor = editAv.accentColor)
                        Box(Modifier.size(130.dp)) {
                            FaceAvatar(spec = editAv, modifier = Modifier.fillMaxSize())
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = if (isFa) "نام جدید" else "New name",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    BasicTextField(
                        value = editName,
                        onValueChange = { if (it.length <= 15) editName = it },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        cursorBrush = Brush.horizontalGradient(
                            listOf(editAv.accentColor, editAv.accentColor.copy(alpha = 0.5f))
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                editAv.accentColor.copy(alpha = 0.12f),
                                                Color(0xFF1E1E2E),
                                                Color(0xFF1A1A28)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (editName.isEmpty()) {
                                    Text(
                                        text = if (isFa) "اینجا بنویس…" else "Type here…",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = TextMuted.copy(alpha = 0.5f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isFa) "۲ تا ۱۵ کاراکتر" else "2 to 15 characters",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(22.dp))

                    Text(
                        text = if (isFa) "آواتار جدید" else "New avatar",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (row in 0 until 3) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (col in 0 until 4) {
                                    val idx = row * 4 + col
                                    if (idx < AVATARS.size) {
                                        val a = AVATARS[idx]
                                        EditAvatarTile(
                                            avatar = a,
                                            selected = a.id == editAvatarId,
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                audio.playSfx(Sfx.CLICK)
                                                editAvatarId = a.id
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            Modifier.weight(1f).height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.horizontalGradient(listOf(Color(0xFF23232F), Color(0xFF1A1A25))))
                                .clickable { audio.playSfx(Sfx.CLICK); editing = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isFa) "لغو" else "Cancel",
                                color = TextMuted,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        val valid = editName.trim().length in 2..15
                        Box(
                            Modifier.weight(1.5f).height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .drawBehind {
                                    if (valid) {
                                        val r = size.maxDimension * 0.65f
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    editAv.accentColor.copy(alpha = 0.35f),
                                                    Color.Transparent
                                                ),
                                                center = center, radius = r
                                            ),
                                            radius = r, center = center
                                        )
                                    }
                                }
                                .background(
                                    if (valid)
                                        Brush.horizontalGradient(
                                            listOf(
                                                editAv.accentColor.copy(alpha = 0.88f),
                                                editAv.accentColor.copy(alpha = 0.55f)
                                            )
                                        )
                                    else
                                        Brush.horizontalGradient(listOf(Color(0xFF23232F), Color(0xFF1A1A25)))
                                )
                                .clickable(enabled = valid) {
                                    val newAv = avatarById(editAvatarId)
                                    val updated = profile.copy(
                                        username = editName.trim(),
                                        avatarId = newAv.id,
                                        editCount = profile.editCount + 1
                                    )
                                    profile = updated
                                    editing = false
                                    scope.launch { dataStore.updateProfile(updated) }
                                    audio.playSfx(Sfx.CORRECT)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (isFa) "ذخیره" else "Save",
                                color = if (valid) Color.White else TextMuted,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }

            // ─── EDIT GATE MODAL ───
            if (showEditGateModal) {
                Box(
                    Modifier.fillMaxSize()
                        .background(Brush.radialGradient(listOf(Color(0xE808080E), Color(0xF808080E))))
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
                    ) {
                        Text(
                            if (isFa) "ویرایش پروفایل" else "Edit profile",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            if (isFa) "ویرایش دوم به بعد هزینه دارد"
                                   else "Further edits require payment",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                        Box(
                            Modifier.clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFFC947).copy(alpha = 0.18f), Color(0xFFFFC947).copy(alpha = 0.06f))
                                    )
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                if (isFa) "${toFaNum(wallet.coins)} سکه" else "${wallet.coins} coins",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFFFC947),
                                maxLines = 1
                            )
                        }
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF6EC8FF).copy(alpha = 0.85f),
                                            Color(0xFF6EC8FF).copy(alpha = 0.55f)
                                        )
                                    )
                                )
                                .clickable { onShopClick() }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                if (isFa) "+ خرید سکه" else "+ Buy coins",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                maxLines = 1
                            )
                        }

                        if (wallet.coins >= Economy.EDIT_PROFILE_PRICE) {
                            Row(
                                Modifier.fillMaxWidth().height(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFFFFC947).copy(alpha = 0.88f),
                                                Color(0xFFFFC947).copy(alpha = 0.62f)
                                            )
                                        )
                                    )
                                    .clickable {
                                        scope.launch {
                                            val w = dataStore.getWallet()
                                            if (w.coins >= Economy.EDIT_PROFILE_PRICE) {
                                                dataStore.updateWallet(w.copy(coins = w.coins - Economy.EDIT_PROFILE_PRICE))
                                                wallet = dataStore.getWallet()
                                                showEditGateModal = false
                                                editName = profile.username
                                                editAvatarId = profile.avatarId
                                                editing = true
                                            }
                                        }
                                    }
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(Modifier.size(20.dp)) {
                                    CoinIcon(modifier = Modifier.fillMaxSize(), tint = Color.White, animate = false)
                                }
                                Spacer(Modifier.size(8.dp))
                                Text(
                                    if (isFa) "خرید با ${toFaNum(Economy.EDIT_PROFILE_PRICE)} سکه"
                                           else "Buy for ${Economy.EDIT_PROFILE_PRICE} coins",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(
                            Modifier.fillMaxWidth().height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(av.accentColor.copy(alpha = 0.22f), av.accentColor.copy(alpha = 0.08f))
                                    )
                                )
                                .clickable(enabled = !adLoading) {
                                    adLoading = true
                                    adError = null
                                    adManager.showRewarded(
                                        onRewarded = {
                                            showEditGateModal = false
                                            adLoading = false
                                            editName = profile.username
                                            editAvatarId = profile.avatarId
                                            editing = true
                                        },
                                        onClosed = { adLoading = false },
                                        onFailed = { msg -> adLoading = false; adError = msg }
                                    )
                                }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (adLoading) {
                                CircularProgressIndicator(color = av.accentColor, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Outlined.Videocam, null, tint = av.accentColor, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.size(8.dp))
                            Text(
                                if (isFa) "تماشای تبلیغ ، ویرایش رایگان"
                                       else "Watch ad — free edit",
                                color = av.accentColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        TextButton(onClick = { showEditGateModal = false }) {
                            Text(if (isFa) "بستن" else "Close", color = TextMuted)
                        }
                    }
                }
            }

            // ─── AD ERROR ───
            adError?.let { err ->
                Box(
                    Modifier.fillMaxSize().background(Color(0xE808080E)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        Modifier.widthIn(max = 320.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF1E1E2E))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            if (isFa) "تبلیغ در دسترس نیست" else "Ad not available",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            err,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                        TextButton(onClick = { adError = null }) {
                            Text(if (isFa) "بستن" else "Close", color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// Helper Composables
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun AvatarHalo(accentColor: Color) {
    val transition = rememberInfiniteTransition(label = "avatarHalo")
    val pulse by transition.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Canvas(Modifier.fillMaxSize()) {
        val r = size.minDimension * 0.6f * pulse
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.35f),
                    accentColor.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = center,
                radius = r
            ),
            radius = r,
            center = center
        )
    }
}

@Composable
private fun GlowingTitleBadge(icon: ImageVector, label: String, color: Color) {
    val transition = rememberInfiniteTransition(label = "titleGlow")
    val glow by transition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    Row(
        Modifier
            .drawBehind {
                val r = size.maxDimension * 0.75f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.30f * glow), Color.Transparent),
                        center = center, radius = r
                    ),
                    radius = r, center = center
                )
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0.10f))
                )
            )
            .padding(horizontal = 18.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    accent: Color,
    icon: @Composable () -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = 0.12f),
                        Color(0xFF1A1A28),
                        Color(0xFF14141D)
                    )
                )
            )
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon()
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                color = accent,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EditAvatarTile(
    avatar: AvatarSpec,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(if (selected) 1.05f else 1f) }
    LaunchedEffect(selected) {
        scale.animateTo(
            if (selected) 1.08f else 1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }
    Box(
        modifier
            .height(62.dp)
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                if (selected) {
                    val r = size.maxDimension * 0.85f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(avatar.accentColor.copy(alpha = 0.45f), Color.Transparent),
                            center = center, radius = r
                        ),
                        radius = r, center = center
                    )
                }
            }
            .background(
                if (selected)
                    Brush.horizontalGradient(
                        listOf(avatar.accentColor.copy(alpha = 0.28f), avatar.accentColor.copy(alpha = 0.10f))
                    )
                else
                    Brush.horizontalGradient(listOf(Color(0xFF23232F), Color(0xFF1A1A25)))
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(46.dp)) {
            FaceAvatar(spec = avatar, modifier = Modifier.fillMaxSize())
        }
    }
}

package ir.numdoz

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import ir.numdoz.ads.LocalAdManager
import ir.numdoz.ads.createAdManager
import ir.numdoz.audio.LocalAudio
import ir.numdoz.audio.rememberAudioManager
import ir.numdoz.data.local.createDataStore
import ir.numdoz.i18n.Lang
import ir.numdoz.i18n.LocalLang
import ir.numdoz.i18n.LocalStrings
import ir.numdoz.i18n.stringsFor
import ir.numdoz.ui.components.FloatingNumbers
import ir.numdoz.ui.components.MinimalBackground
import ir.numdoz.ui.navigation.Screen
import ir.numdoz.ui.screens.ChapterMonthsScreen
import ir.numdoz.ui.screens.ChaptersScreen
import ir.numdoz.ui.screens.LevelPlayScreen
import ir.numdoz.ui.screens.MonthDaysScreen
import ir.numdoz.ui.screens.SplashScreen
import ir.numdoz.ui.screens.ProfileScreen
import ir.numdoz.ui.screens.ProfileSetupScreen
import ir.numdoz.data.model.UserProfile
import ir.numdoz.util.currentTimeMillis
import ir.numdoz.ui.screens.YearListScreen
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.LocalAccentAlt
import ir.numdoz.ui.theme.LocalAccentTertiary
import ir.numdoz.ui.theme.NumDozTheme
import ir.numdoz.ui.theme.rememberSpectrumHue
import ir.numdoz.ui.theme.spectrumColor

@Composable
fun App() {
    val audio = rememberAudioManager()
    val scope = rememberCoroutineScope()
    val adManager = remember { createAdManager() }
    val dataStore = remember { createDataStore() }
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var tint by remember { mutableStateOf<Color?>(null) }
    var lang by remember { mutableStateOf(Lang.FA) }
    var userProfile by remember { mutableStateOf(UserProfile()) }

    LaunchedEffect(Unit) {
        try {
            adManager.initialize()
            dataStore.ensureSchema()
            dataStore.seedIfEmpty()
            val settings = dataStore.getSettings()
            lang = if (settings.language == "en") Lang.EN else Lang.FA

            val loaded = dataStore.getProfile()
            userProfile = loaded
            if (loaded.exists) {
                dataStore.updateProfile(
                    loaded.copy(
                        lastLoginAt = currentTimeMillis(),
                        totalSessions = loaded.totalSessions + 1
                    )
                )
            }
            println("[NumDoz/app] boot complete, lang=${lang.name}, user=${loaded.username}")
        } catch (t: Throwable) {
            println("[NumDoz/app] boot error: ${t.message}")
        }
    }

    val strings = remember(lang) { stringsFor(lang) }

    val hueA = rememberSpectrumHue(cycleSeconds = 120, phaseOffset = 0f)
    val hueB = rememberSpectrumHue(cycleSeconds = 120, phaseOffset = 180f)
    val accentA = spectrumColor(hueA)
    val accentB = spectrumColor(hueB)
    val hueC = rememberSpectrumHue(cycleSeconds = 120, phaseOffset = 100f)
    val accentC = spectrumColor(hueC)

    CompositionLocalProvider(
        LocalAudio provides audio,
        LocalAdManager provides adManager,
        LocalAccent provides accentA,
        LocalAccentAlt provides accentB,
        LocalAccentTertiary provides accentC,
        LocalLang provides lang,
        LocalStrings provides strings
    ) {
        NumDozTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                MinimalBackground(overrideAccent = tint) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FloatingNumbers(count = 32, color = tint ?: accentA)
                        Crossfade(
                            targetState = screen,
                            animationSpec = tween(320, easing = FastOutSlowInEasing),
                            label = "nav"
                        ) { s ->
                            when (s) {
                                Screen.Splash -> SplashScreen(
                                    onDone = {
                                        tint = null
                                        screen = if (userProfile.exists) Screen.YearList else Screen.ProfileSetup
                                    }
                                )

                                Screen.ProfileSetup -> ProfileSetupScreen(
                                    isFa = lang == Lang.FA,
                                    onSave = { username, avatarId ->
                                        scope.launch {
                                            val fresh = dataStore.getProfile()
                                            val now = currentTimeMillis()
                                            val newProfile = UserProfile(
                                                username = username,
                                                avatarId = avatarId,
                                                createdAt = if (fresh.createdAt > 0) fresh.createdAt else now,
                                                lastLoginAt = now,
                                                totalSessions = fresh.totalSessions + 1
                                            )
                                            dataStore.updateProfile(newProfile)
                                            userProfile = newProfile
                                            screen = Screen.YearList
                                        }
                                    }
                                )

                                Screen.Profile -> ProfileScreen(
                                    dataStore = dataStore,
                                    onBack = { screen = Screen.YearList }
                                )
                                Screen.YearList -> YearListScreen(
                                    dataStore = dataStore,
                                    currentLang = lang,
                                    onLanguageChange = { newLang -> lang = newLang },
                                    onTintChange = { tint = it },
                                    onYearClick = { yearId -> screen = Screen.Chapters(yearId) },
                                    onProfileClick = { screen = Screen.Profile },
                                )
                                is Screen.Chapters -> ChaptersScreen(
                                    yearId = s.yearId,
                                    dataStore = dataStore,
                                    onTintChange = { tint = it },
                                    onBack = {
                                        tint = null
                                        screen = Screen.YearList
                                    },
                                    onChapterClick = { chId ->
                                        screen = Screen.ChapterMonths(s.yearId, chId)
                                    }
                                )
                                is Screen.ChapterMonths -> ChapterMonthsScreen(
                                    yearId = s.yearId,
                                    chapterId = s.chapterId,
                                    dataStore = dataStore,
                                    onTintChange = { tint = it },
                                    onBack = { screen = Screen.Chapters(s.yearId) },
                                    onMonthClick = { secId ->
                                        screen = Screen.MonthDays(s.yearId, s.chapterId, secId)
                                    }
                                )
                                is Screen.MonthDays -> MonthDaysScreen(
                                    yearId = s.yearId,
                                    chapterId = s.chapterId,
                                    sectionId = s.sectionId,
                                    dataStore = dataStore,
                                    onTintChange = { tint = it },
                                    onBack = { screen = Screen.ChapterMonths(s.yearId, s.chapterId) },
                                    onLevelClick = { lvId ->
                                        screen = Screen.LevelPlay(lvId, s.chapterId, s.sectionId, s.yearId)
                                    }
                                )
                                is Screen.LevelPlay -> LevelPlayScreen(
                                    levelId = s.levelId,
                                    chapterId = s.chapterId,
                                    sectionId = s.sectionId,
                                    dataStore = dataStore,
                                    onTintChange = { tint = it },
                                    onBack = {
                                        screen = Screen.MonthDays(s.yearId, s.chapterId, s.sectionId)
                                    },
                                    onNext = { nextId ->
                                        screen = Screen.LevelPlay(nextId, s.chapterId, s.sectionId, s.yearId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package ir.numdoz.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.Year

enum class Lang { FA, EN }

data class Strings(
    // ─── Common ───
    val close: String,
    val comingSoon: String,
    val comingSoonShort: String,
    val watchAd: String,
    val ad: String,
    
    // ─── Splash ───
    val tagline: String,
    
    // ─── Home / Years ───
    val yearsLabel: String,
    val hintLabel: String,
    val noHintsLabel: String,
    val settingsTitle: String,
    val languageLabel: String,
    val languageFa: String,
    val languageEn: String,
    val soundLabel: String,
    val soundOn: String,
    val soundOff: String,
    val resetProgressLabel: String,
    val aboutLabel: String,
    
    // ─── Hint modal ───
    val youHaveNoHintsTitle: String,
    val youHaveNoHintsDesc: String,
    val hintModalDesc: String,
    val hintModalDescEmpty: String,
    val watchAdForHint: String,
    val buyHintComing: String,
    val adRewardMessage: String,
    
    // ─── Chapters / Months ───
    val seasonsLabel: String,
    val monthsLabel: String,
    
    // ─── Level play ───
    val completeSequence: String,
    val answerPlaceholder: String,
    val submit: String,
    val nextLevel: String,
    val backToList: String,
    val timesUp: String,
    val watchAdForTime: String,
    val backWithoutContinue: String,
    val wellDone: String,
    
    // ─── Intro dialog ───
    val enterYear: String,
    val enterSeason: String,
    val enterMonth: String,
    
    // ─── Functions ───
    val hintWithCount: (Int) -> String,
    val youHaveNHints: (Int) -> String,
    val countdown: (Int) -> String,
    val timeLabel: (String) -> String,
    val hintsUsedLabel: (Int) -> String,
    val progressLevels: (Int, Int) -> String,
    val progressDays: (Int, Int) -> String,
    val completedStats: (Int, Int) -> String,
    val dayLabel: (Int) -> String,
)

private val FaStrings = Strings(
    close = "بستن",
    comingSoon = "به زودی",
    comingSoonShort = "(به زودی)",
    watchAd = "تماشای تبلیغ",
    ad = "تبلیغ",
    tagline = "عددچین",
    yearsLabel = "سال‌ها",
    hintLabel = "راهنما",
    noHintsLabel = "بدون راهنما",
    settingsTitle = "تنظیمات",
    languageLabel = "زبان",
    languageFa = "فارسی",
    languageEn = "English",
    soundLabel = "صدا",
    soundOn = "روشن",
    soundOff = "خاموش",
    resetProgressLabel = "بازنشانی پیشرفت",
    aboutLabel = "درباره",
    youHaveNoHintsTitle = "شما راهنما ندارید",
    youHaveNoHintsDesc = "برای دریافت راهنما یکی از گزینه‌های زیر را انتخاب کنید",
    hintModalDesc = "هنگام بازی با کلیک روی آیکون راهنما، یک عدد از این تعداد کم می‌شود.",
    hintModalDescEmpty = "برای دریافت راهنما می‌توانید یک تبلیغ کوتاه ببینید یا راهنما خریداری کنید.",
    watchAdForHint = "تماشای تبلیغ — دریافت ۱ راهنما",
    buyHintComing = "خرید راهنما (به‌زودی)",
    adRewardMessage = "بعد از شمارش، ۱ راهنما به شما داده می‌شود",
    seasonsLabel = "فصل‌ها",
    monthsLabel = "ماه‌ها",
    completeSequence = "دنباله را کامل کنید",
    answerPlaceholder = "پاسخ…",
    submit = "تأیید",
    nextLevel = "مرحله‌ی بعد",
    backToList = "بازگشت به فهرست",
    timesUp = "زمان تمام شد",
    watchAdForTime = "برای ادامه یک تبلیغ ببینید تا ۱ دقیقه اضافه شود",
    backWithoutContinue = "بازگشت بدون ادامه",
    wellDone = "آفرین!",
    enterYear = "ورود به سال",
    enterSeason = "ورود به فصل",
    enterMonth = "ورود به ماه",
    hintWithCount = { n -> "راهنما · $n" },
    youHaveNHints = { n -> "دارای $n راهنما" },
    countdown = { n -> "شمارش معکوس: $n" },
    timeLabel = { t -> "زمان: $t" },
    hintsUsedLabel = { n -> "$n راهنما استفاده شد" },
    progressLevels = { d, t -> "$d از $t مرحله" },
    progressDays = { d, t -> "$d از $t روز" },
    completedStats = { d, t -> "$d از $t مرحله تکمیل شده" },
    dayLabel = { n -> "روز $n" },
)

private val EnStrings = Strings(
    close = "Close",
    comingSoon = "Coming soon",
    comingSoonShort = "(Coming soon)",
    watchAd = "Watch ad",
    ad = "Ad",
    tagline = "Number Puzzles",
    yearsLabel = "Years",
    hintLabel = "Hint",
    noHintsLabel = "No hints",
    settingsTitle = "Settings",
    languageLabel = "Language",
    languageFa = "فارسی",
    languageEn = "English",
    soundLabel = "Sound",
    soundOn = "On",
    soundOff = "Off",
    resetProgressLabel = "Reset progress",
    aboutLabel = "About",
    youHaveNoHintsTitle = "You have no hints",
    youHaveNoHintsDesc = "Choose one of the options below to get a hint",
    hintModalDesc = "During gameplay, clicking the hint icon uses one of these.",
    hintModalDescEmpty = "To get a hint, you can watch a short ad or buy hints.",
    watchAdForHint = "Watch ad — Get 1 hint",
    buyHintComing = "Buy hint (Coming soon)",
    adRewardMessage = "You will receive 1 hint after the countdown",
    seasonsLabel = "Seasons",
    monthsLabel = "Months",
    completeSequence = "Complete the sequence",
    answerPlaceholder = "Answer…",
    submit = "Submit",
    nextLevel = "Next level",
    backToList = "Back to list",
    timesUp = "Time's up",
    watchAdForTime = "Watch an ad to get +1 minute",
    backWithoutContinue = "Go back without continuing",
    wellDone = "Well done!",
    enterYear = "Enter year",
    enterSeason = "Enter season",
    enterMonth = "Enter month",
    hintWithCount = { n -> "Hint · $n" },
    youHaveNHints = { n -> "You have $n hints" },
    countdown = { n -> "Countdown: $n" },
    timeLabel = { t -> "Time: $t" },
    hintsUsedLabel = { n -> "$n hints used" },
    progressLevels = { d, t -> "$d of $t levels" },
    progressDays = { d, t -> "$d of $t days" },
    completedStats = { d, t -> "$d of $t levels completed" },
    dayLabel = { n -> "Day $n" },
)

fun stringsFor(lang: Lang): Strings = when (lang) {
    Lang.FA -> FaStrings
    Lang.EN -> EnStrings
}

val LocalLang = staticCompositionLocalOf { Lang.FA }
val LocalStrings = staticCompositionLocalOf { FaStrings }

// ─── Data display helpers ───
fun Year.displayTitle(lang: Lang): String = if (lang == Lang.FA) titleFa else titleEn
fun Year.displayDescription(lang: Lang): String = if (lang == Lang.FA) descriptionFa else descriptionEn

fun Chapter.displayTitle(lang: Lang): String = if (lang == Lang.FA) titleFa else titleEn
fun Chapter.displayDescription(lang: Lang): String = if (lang == Lang.FA) descriptionFa else descriptionEn

fun Section.displayTitle(lang: Lang): String = if (lang == Lang.FA) titleFa else titleEn
fun Section.displayDescription(lang: Lang): String = if (lang == Lang.FA) descriptionFa else descriptionEn

fun Level.displayHint1(lang: Lang): String = if (lang == Lang.FA) hint1Fa else hint1En
fun Level.displayHint2(lang: Lang): String = if (lang == Lang.FA) hint2Fa else hint2En

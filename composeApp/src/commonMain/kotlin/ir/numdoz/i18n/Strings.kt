package ir.numdoz.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.Year

enum class Lang { FA, EN }

/** تبدیل ارقام لاتین به فارسی برای جلوگیری از مشکل BiDi */
fun toFaNum(n: Int): String =
    n.toString().map { ch -> if (ch in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[ch - '0'] else ch }.joinToString("")

fun toFaNum(s: String): String =
    s.map { ch -> if (ch in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[ch - '0'] else ch }.joinToString("")

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

    // ─── Coin economy ───
    val coinsLabel: String,
    val getCoins: String,
    val watchAdForCoins: String,
    val notEnoughCoins: String,
    val claimDailyReward: String,
    val dailyAlreadyClaimed: String,
    val addStarWithCoins: String,
    val timeBoostLabel: String,
    val noTimeBoosts: String,
    val watchAdForTimeBoost: String,
    val timeoutFailedTitle: String,
    val timeoutFailedDesc: String,
    val adForHintAfterEmpty: String,

    // ─── Functions (خروجی آماده) ───
    val hintWithCount: (Int) -> String,
    val youHaveNHints: (Int) -> String,
    val countdown: (Int) -> String,
    val timeLabel: (String) -> String,
    val hintsUsedLabel: (Int) -> String,
    val progressLevels: (Int, Int) -> String,
    val progressDays: (Int, Int) -> String,
    val completedStats: (Int, Int) -> String,
    val dayLabel: (Int) -> String,
    val coinCount: (Int) -> String,
    val buyWithCoins: (Int) -> String,
    val coinsEarned: (Int) -> String,
    val coinsSpent: (Int) -> String,
    val dailyRewardCoins: (Int) -> String,
    val timeBoostWithCount: (Int) -> String,
    val timeLabelFmt: (String) -> String,
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
    youHaveNoHintsDesc = "برای دریافت راهنما یکی از گزینه‌ها را انتخاب کنید",
    hintModalDesc = "با کلیک روی آیکون راهنما، یک عدد از این تعداد کم می‌شود.",
    hintModalDescEmpty = "برای دریافت راهنما یک تبلیغ ببینید یا راهنما بخرید.",
    watchAdForHint = "تماشای تبلیغ ، راهنمای رایگان",
    buyHintComing = "خرید راهنما (به‌زودی)",
    adRewardMessage = "بعد از شمارش، جایزه‌تان داده می‌شود",

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

    coinsLabel = "سکه",
    getCoins = "دریافت سکه",
    watchAdForCoins = "تماشای تبلیغ ، ۱۰ سکه",
    notEnoughCoins = "سکه کافی نیست",
    claimDailyReward = "دریافت جایزه",
    dailyAlreadyClaimed = "امروز دریافت شد",
    addStarWithCoins = "خرید ستاره",
    timeBoostLabel = "کپسول زمان",
    noTimeBoosts = "بدون کپسول",
    watchAdForTimeBoost = "تماشای تبلیغ ، کپسول رایگان",
    timeoutFailedTitle = "زمان تمام شد",
    timeoutFailedDesc = "این تلاش ثبت شد. دفعه بعد با زمان کمتری شروع می‌کنید.",
    adForHintAfterEmpty = "برای راهنمای دوم، یک تبلیغ ببینید",

    hintWithCount = { n -> "راهنما · ${toFaNum(n)}" },
    youHaveNHints = { n -> "دارای ${toFaNum(n)} راهنما" },
    countdown = { n -> "شمارش معکوس: ${toFaNum(n)}" },
    timeLabel = { t -> "زمان: ${toFaNum(t)}" },
    hintsUsedLabel = { n -> "${toFaNum(n)} راهنما استفاده شد" },
    progressLevels = { d, t -> "${toFaNum(d)} از ${toFaNum(t)} مرحله" },
    progressDays = { d, t -> "${toFaNum(d)} از ${toFaNum(t)} روز" },
    completedStats = { d, t -> "${toFaNum(d)} از ${toFaNum(t)} مرحله تکمیل شده" },
    dayLabel = { n -> "روز ${toFaNum(n)}" },
    coinCount = { n -> "${toFaNum(n)} سکه" },
    buyWithCoins = { price -> "خرید با ${toFaNum(price)} سکه" },
    coinsEarned = { n -> "دریافت ${toFaNum(n)} سکه" },
    coinsSpent = { n -> "صرف ${toFaNum(n)} سکه" },
    dailyRewardCoins = { n -> "جایزه: ${toFaNum(n)} سکه" },
    timeBoostWithCount = { n -> "کپسول · ${toFaNum(n)}" },
    timeLabelFmt = { t -> "زمان: ${toFaNum(t)}" },
)

private val EnStrings = Strings(
    close = "Close",
    comingSoon = "Coming soon",
    comingSoonShort = "(Soon)",
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
    youHaveNoHintsDesc = "Choose an option to get a hint",
    hintModalDesc = "Clicking the hint icon uses one of these.",
    hintModalDescEmpty = "Watch a short ad or buy hints.",
    watchAdForHint = "Watch ad — free hint",
    buyHintComing = "Buy hint (Coming soon)",
    adRewardMessage = "You will receive your reward after the countdown",

    seasonsLabel = "Seasons",
    monthsLabel = "Months",

    completeSequence = "Complete the sequence",
    answerPlaceholder = "Answer…",
    submit = "Submit",
    nextLevel = "Next level",
    backToList = "Back to list",
    timesUp = "Time's up",
    watchAdForTime = "Watch an ad to get +1 minute",
    backWithoutContinue = "Back without continuing",
    wellDone = "Well done!",

    enterYear = "Enter year",
    enterSeason = "Enter season",
    enterMonth = "Enter month",

    coinsLabel = "Coins",
    getCoins = "Get coins",
    watchAdForCoins = "Watch ad — 10 coins",
    notEnoughCoins = "Not enough coins",
    claimDailyReward = "Claim reward",
    dailyAlreadyClaimed = "Claimed today",
    addStarWithCoins = "Buy star",
    timeBoostLabel = "Time Capsule",
    noTimeBoosts = "No capsules",
    watchAdForTimeBoost = "Watch ad — free capsule",
    timeoutFailedTitle = "Time's up",
    timeoutFailedDesc = "This attempt was recorded. You'll start with less time next try.",
    adForHintAfterEmpty = "Watch an ad for the second hint",

    hintWithCount = { n -> "Hint · $n" },
    youHaveNHints = { n -> "You have $n hints" },
    countdown = { n -> "Countdown: $n" },
    timeLabel = { t -> "Time: $t" },
    hintsUsedLabel = { n -> "$n hints used" },
    progressLevels = { d, t -> "$d of $t levels" },
    progressDays = { d, t -> "$d of $t days" },
    completedStats = { d, t -> "$d of $t levels completed" },
    dayLabel = { n -> "Day $n" },
    coinCount = { n -> "$n coins" },
    buyWithCoins = { price -> "Buy for $price coins" },
    coinsEarned = { n -> "+$n coins" },
    coinsSpent = { n -> "−$n coins" },
    dailyRewardCoins = { n -> "Reward: $n coins" },
    timeBoostWithCount = { n -> "Capsule · $n" },
    timeLabelFmt = { t -> "Time: $t" },
)

fun stringsFor(lang: Lang): Strings = when (lang) {
    Lang.FA -> FaStrings
    Lang.EN -> EnStrings
}

val LocalLang = staticCompositionLocalOf { Lang.FA }
val LocalStrings = staticCompositionLocalOf { FaStrings }

// ─── Data display helpers ───
// ─── Data display helpers — با NameMapper و fallback ───
fun Year.displayTitle(lang: Lang): String =
    NameMapper.yearTitle(id, lang) ?: (if (lang == Lang.FA) titleFa else titleEn)
fun Year.displayDescription(lang: Lang): String =
    NameMapper.yearDesc(id, lang) ?: (if (lang == Lang.FA) descriptionFa else descriptionEn)

fun Chapter.displayTitle(lang: Lang): String =
    NameMapper.chapterTitle(id, lang) ?: (if (lang == Lang.FA) titleFa else titleEn)
fun Chapter.displayDescription(lang: Lang): String =
    NameMapper.chapterDesc(id, lang) ?: (if (lang == Lang.FA) descriptionFa else descriptionEn)

fun Section.displayTitle(lang: Lang): String =
    NameMapper.sectionTitle(id, lang) ?: (if (lang == Lang.FA) titleFa else titleEn)
fun Section.displayDescription(lang: Lang): String =
    NameMapper.sectionDesc(id, lang) ?: (if (lang == Lang.FA) descriptionFa else descriptionEn)

fun Level.displayHint1(lang: Lang): String = if (lang == Lang.FA) hint1Fa else hint1En
fun Level.displayHint2(lang: Lang): String = if (lang == Lang.FA) hint2Fa else hint2En
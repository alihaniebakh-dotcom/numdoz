package ir.numdoz.i18n

/**
 * Dynamic name mapping — Concept A: Four Realms.
 * Applied at display time, so SeedData.kt and DB stay untouched.
 */
object NameMapper {

    private data class Entry(val fa: String, val en: String)

    private val yearTitles = mapOf(
        "arka" to Entry("سال نخست · طلوع", "First Year · Dawn"),
        "lumena" to Entry("سال دوم · آفرینش", "Second Year · Genesis")
    )

    private val yearDescriptions = mapOf(
        "arka" to Entry("سفری از نور به راز", "A journey from light to mystery"),
        "lumena" to Entry("دنیایی از نو بساز", "Build a world anew")
    )

    private val chapterTitles = mapOf(
        "kalira" to Entry("قلمروی نور", "Realm of Light"),
        "soreva" to Entry("قلمروی آب", "Realm of Water"),
        "vexira" to Entry("قلمروی آتش", "Realm of Fire"),
        "nivala" to Entry("قلمروی راز", "Realm of Mystery")
    )

    private val chapterDescriptions = mapOf(
        "kalira" to Entry("آغاز، امید و سادگی", "Beginning, hope and simplicity"),
        "soreva" to Entry("جریان، انعطاف و آرامش", "Flow, flexibility and calm"),
        "vexira" to Entry("شور، قدرت و چالش", "Passion, power and challenge"),
        "nivala" to Entry("شب، بصیرت و تسلط", "Night, insight and mastery")
    )

    private val sectionTitles = mapOf(
        "kalira_lumis" to Entry("سپیده", "Dawn"),
        "kalira_auriel" to Entry("طلوع", "Sunrise"),
        "kalira_zephra" to Entry("روز", "Day"),
        "soreva_tidalis" to Entry("جویبار", "Stream"),
        "soreva_aqualin" to Entry("موج", "Wave"),
        "soreva_mistrel" to Entry("اقیانوس", "Ocean"),
        "vexira_emberis" to Entry("شراره", "Spark"),
        "vexira_pyrosa" to Entry("شعله", "Flame"),
        "vexira_solmar" to Entry("آتشفشان", "Volcano"),
        "nivala_frostel" to Entry("شفق", "Aurora"),
        "nivala_nyxara" to Entry("کهکشان", "Galaxy"),
        "nivala_glacira" to Entry("سیاه‌چاله", "Black Hole")
    )

    private val sectionDescriptions = mapOf(
        "kalira_lumis" to Entry("نخستین نور", "First light"),
        "kalira_auriel" to Entry("خورشید زرین", "Golden sun"),
        "kalira_zephra" to Entry("شکوفایی کامل", "Full bloom"),
        "soreva_tidalis" to Entry("نخستین جریان", "First current"),
        "soreva_aqualin" to Entry("رازهای ژرف", "Deep secrets"),
        "soreva_mistrel" to Entry("پرده‌های مه", "Veils of mist"),
        "vexira_emberis" to Entry("نخستین اخگر", "First ember"),
        "vexira_pyrosa" to Entry("شعله‌های سرکش", "Wild flames"),
        "vexira_solmar" to Entry("غروب سرخ", "Red sunset"),
        "nivala_frostel" to Entry("شبنم نخستین", "First frost"),
        "nivala_nyxara" to Entry("آسمان شب", "Night sky"),
        "nivala_glacira" to Entry("یخچال ابدی", "Eternal glacier")
    )

    fun yearTitle(id: String, lang: Lang): String? =
        yearTitles[id]?.let { if (lang == Lang.FA) it.fa else it.en }

    fun yearDesc(id: String, lang: Lang): String? =
        yearDescriptions[id]?.let { if (lang == Lang.FA) it.fa else it.en }

    fun chapterTitle(id: String, lang: Lang): String? =
        chapterTitles[id]?.let { if (lang == Lang.FA) it.fa else it.en }

    fun chapterDesc(id: String, lang: Lang): String? =
        chapterDescriptions[id]?.let { if (lang == Lang.FA) it.fa else it.en }

    fun sectionTitle(id: String, lang: Lang): String? =
        sectionTitles[id]?.let { if (lang == Lang.FA) it.fa else it.en }

    fun sectionDesc(id: String, lang: Lang): String? =
        sectionDescriptions[id]?.let { if (lang == Lang.FA) it.fa else it.en }
}
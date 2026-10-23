package ir.numdoz.data.seed

import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.PuzzleType
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.Year

object SeedData {

    val years: List<Year> = listOf(
        Year("arka", 0, "سال آرکا", "Year of Arka",
            "نخستین سال تقویم عددچین. سفری میان چهار فصل ناشناخته.",
            "First year of the NumDoz calendar.",
            "#4FE3E0", "star", isLocked = false),
        Year("lumena", 1, "سال لومِنا", "Year of Lumena",
            "دومین سال تقویم — سفری به سرزمین‌های نور و راز. به‌زودی...",
            "Second year — journey through light and mystery. Coming soon.",
            "#F59E0B", "brightness", isLocked = true)
    )

    val chapters: List<Chapter> = listOf(
        Chapter("kalira", "arka", 0, "کالیرا", "Kalira",
            "سپیده‌دم — آغاز و روشنایی", "Dawn — beginning and light",
            "#F59E0B", "sun", false),
        Chapter("soreva", "arka", 1, "سورِوا", "Soreva",
            "اقیانوس — جریان و آرامش", "Ocean — flow and calm",
            "#0EA5E9", "wave", false),
        Chapter("vexira", "arka", 2, "وکسیرا", "Vexira",
            "آتش — قدرت و گرما", "Fire — power and heat",
            "#EF4444", "flame", false),
        Chapter("nivala", "arka", 3, "نیوالا", "Nivala",
            "یخ — راز و سکون", "Ice — mystery and stillness",
            "#A855F7", "snow", false)
    )

    val sections: List<Section> = listOf(
        Section("kalira_lumis", "kalira", 0, "لومیس", "Lumis", 0,
            "نخستین ماه از فصل کالیرا — ماه بیداری نور و آغاز سفر",
            "First month of Kalira — awakening of light",
            "sunrise", "#F59E0B"),
        Section("kalira_auriel", "kalira", 1, "آئوریل", "Auriel", 1,
            "دومین ماه — ماه طلوع طلایی و شکوفایی کامل",
            "Second month — golden dawn",
            "brightness", "#FB923C"),
        Section("kalira_zephra", "kalira", 2, "زِفرا", "Zephra", 2,
            "سومین ماه — ماه نسیم شکوفه و شکوفایی طبیعت",
            "Third month — blossom breeze",
            "wind", "#FCD34D"),
        Section("soreva_tidalis", "soreva", 0, "تیدالیس", "Tidalis", 0,
            "نخستین ماه از فصل سورِوا — ماه نخستین موج و آغاز جریان",
            "First month of Soreva — first wave",
            "wave", "#0EA5E9"),
        Section("soreva_aqualin", "soreva", 1, "آکوآلین", "Aqualin", 1,
            "دومین ماه — ماه ژرفای آب و رازهای اقیانوس",
            "Second month — deep water",
            "water", "#0284C7"),
        Section("soreva_mistrel", "soreva", 2, "میسترِل", "Mistrel", 2,
            "سومین ماه — ماه مه و پرده‌های اسرارآمیز دریا",
            "Third month — mist",
            "cloud", "#7DD3FC"),
        Section("vexira_emberis", "vexira", 0, "امبریس", "Emberis", 0,
            "نخستین ماه از فصل وکسیرا — ماه اخگر و آغاز گرما",
            "First month of Vexira — ember",
            "ember", "#EF4444"),
        Section("vexira_pyrosa", "vexira", 1, "پایروزا", "Pyrosa", 1,
            "دومین ماه — ماه شعله‌های سرکش و قدرت آتش",
            "Second month — flame",
            "flame", "#DC2626"),
        Section("vexira_solmar", "vexira", 2, "سولمار", "Solmar", 2,
            "سومین ماه — ماه غروب سرخ و آخرین گرمای خورشید",
            "Third month — sunset",
            "sunset", "#F97316"),
        Section("nivala_frostel", "nivala", 0, "فروستل", "Frostel", 0,
            "نخستین ماه از فصل نیوالا — ماه نخستین شبنم یخ و آغاز سکون",
            "First month of Nivala — first frost",
            "snow", "#A855F7"),
        Section("nivala_nyxara", "nivala", 1, "نیکسارا", "Nyxara", 1,
            "دومین ماه — ماه آسمان شب و ستارگان سرد",
            "Second month — night sky",
            "moon", "#8B5CF6"),
        Section("nivala_glacira", "nivala", 2, "گلاسیرا", "Glacira", 2,
            "سومین ماه — ماه یخچال و ژرفای سرمای ابدی",
            "Third month — glacier",
            "star", "#C4B5FD")
    )

    val levels: List<Level> = buildList {
        sections.forEachIndexed { globalIdx, sec ->
            for (day in 1..30) add(generateLevel(sec.id, day, globalIdx))
        }
    }

    private fun lvl(secId: String, day: Int, type: PuzzleType, difficulty: Int,
                    seq: String, ans: String, titleFa: String, titleEn: String,
                    h1fa: String, h1en: String, h2fa: String, h2en: String,
                    efa: String, een: String): Level = Level(
        id = "${secId}_d${day.toString().padStart(2, '0')}",
        sectionId = secId, orderIndex = day - 1, dayNumber = day,
        titleFa = titleFa, titleEn = titleEn, puzzleType = type,
        difficulty = difficulty, timeLimitSeconds = 180,
        sequence = seq, correctAnswer = ans,
        hint1Fa = h1fa, hint1En = h1en, hint2Fa = h2fa, hint2En = h2en,
        explanationFa = efa, explanationEn = een
    )

    private fun arithInc(secId: String, day: Int, start: Int, step: Int, diff: Int): Level {
        val seq = (0 until 5).map { start + step * it }
        val ans = start + step * 5
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "دنباله‌ی افزایشی", "Ascending",
            "به اختلاف دو عدد پی‌درپی نگاه کن", "Look at consecutive differences",
            "هر بار $step تا اضافه می‌شه", "Add $step each time",
            "دنباله‌ی حسابی با گام $step", "Arithmetic, step $step")
    }

    private fun arithDec(secId: String, day: Int, start: Int, step: Int, diff: Int): Level {
        val seq = (0 until 5).map { start - step * it }
        val ans = start - step * 5
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "دنباله‌ی کاهشی", "Descending",
            "هر عدد کوچک‌تر است", "Each is smaller",
            "هر بار $step تا کم می‌شه", "Subtract $step each time",
            "دنباله‌ی نزولی با گام $step", "Descending, step $step")
    }

    private fun geo(secId: String, day: Int, start: Int, ratio: Int, diff: Int): Level {
        val seq = mutableListOf(start)
        for (i in 1 until 4) seq += seq.last() * ratio
        val ans = seq.last() * ratio
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "دنباله‌ی هندسی", "Geometric",
            "هر عدد چند برابر قبلی؟", "How many times previous?",
            "هر بار در $ratio ضرب می‌شه", "Multiply by $ratio",
            "تصاعد هندسی، نسبت $ratio", "Geometric, ratio $ratio")
    }

    private fun squares(secId: String, day: Int, startN: Int, diff: Int): Level {
        val seq = (0 until 4).map { val v = startN + it; v * v }
        val last = startN + 4
        val ans = (last * last).toString()
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "مربع اعداد", "Squares",
            "۱×۱، ۲×۲، ۳×۳", "1×1, 2×2, 3×3",
            "توان دوم اعداد طبیعی", "Squares of naturals",
            "مربع از $startN تا $last", "Squares $startN..$last")
    }

    private fun cubes(secId: String, day: Int, startN: Int, diff: Int): Level {
        val seq = (0 until 4).map { val v = startN + it; v * v * v }
        val last = startN + 4
        val ans = (last * last * last).toString()
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "مکعب اعداد", "Cubes",
            "توان سوم اعداد", "Cubes of naturals",
            "۱³، ۲³، ۳³، ۴³", "1³, 2³, 3³, 4³",
            "مکعب از $startN تا $last", "Cubes $startN..$last")
    }

    private fun fib(secId: String, day: Int, a: Int, b: Int, diff: Int): Level {
        val seq = mutableListOf(a, b)
        for (i in 2 until 5) seq += seq[i - 1] + seq[i - 2]
        val ans = seq.last() + seq[seq.size - 2]
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "فیبوناچی", "Fibonacci",
            "جمع دو عدد قبلی؟", "Sum of previous two?",
            "هر عدد = دو عدد قبل", "Each = sum of previous two",
            "دنباله‌ی فیبوناچی", "Fibonacci")
    }

    private fun triangular(secId: String, day: Int, start: Int, diff: Int): Level {
        val seq = mutableListOf(start)
        for (i in 1 until 5) seq += seq.last() + (start + i)
        val ans = seq.last() + (start + 5)
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "اعداد مثلثی", "Triangular",
            "به اختلاف‌ها: ۲، ۳، ۴", "Differences: 2,3,4",
            "اختلاف‌ها یک واحد زیاد می‌شن", "Differences +1",
            "دنباله‌ی مثلثی", "Triangular")
    }

    private fun primes(secId: String, day: Int, startIdx: Int, diff: Int): Level {
        val primes = listOf(2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97)
        val seq = primes.drop(startIdx).take(4)
        val ans = primes[startIdx + 4]
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "اعداد اول", "Primes",
            "به بخش‌پذیری فکر کن", "Divisibility",
            "اعداد اول پشت سر هم", "Consecutive primes",
            "دنباله‌ی اعداد اول", "Primes")
    }

    private fun lettersSkip(secId: String, day: Int, start: Char, step: Int, diff: Int): Level {
        val seq = (0 until 4).map { (start + step * it).toString() }
        val ans = (start + step * 4).toString()
        val stepFa = if (step > 0) "پرش $step" else "پرش رو به عقب"
        return lvl(secId, day, PuzzleType.LETTER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "حروف با پرش", "Skipping letters",
            "به فاصله‌ی حروف نگاه کن", "Look at letter gaps",
            stepFa, "Skip $step",
            "حروف با فاصله‌ی $step", "Letters skip $step")
    }

    private fun letterNumber(secId: String, day: Int, startNum: Int, diff: Int): Level {
        val seq = (0 until 4).map { i -> val n = startNum + i; "${('A' + (n - 1))}$n" }
        val lastN = startNum + 4
        val ans = "${('A' + (lastN - 1))}$lastN"
        return lvl(secId, day, PuzzleType.MIXED, diff,
            seq.joinToString(", ") + ", ?", ans,
            "حرف و عدد", "Letter + number",
            "هر حرف با یک عدد", "Each letter with a number",
            "حرف و عدد هم‌راستا", "Letter with number",
            "ترکیب حرف و عدد", "Letter-number")
    }

    private fun alternating(secId: String, day: Int, start: Int, plusA: Int, minusB: Int, diff: Int): Level {
        val seq = mutableListOf(start)
        for (i in 1 until 5) {
            if (i % 2 == 1) seq += seq.last() + plusA else seq += seq.last() - minusB
        }
        val ans = if (5 % 2 == 1) seq.last() + plusA else seq.last() - minusB
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "جمع و تفریق متناوب", "Alternating",
            "به جهت تغییرات نگاه کن", "Direction of changes",
            "یکی زیاد، یکی کم", "One up, one down",
            "الگوی متناوب", "Alternating")
    }

    private fun twoStep(secId: String, day: Int, start: Int, diff: Int): Level {
        val seq = mutableListOf(start)
        for (i in 1 until 5) seq += seq.last() + i
        val ans = seq.last() + 5
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "گام افزایشی", "Increasing steps",
            "به اختلاف‌ها نگاه کن", "Look at differences",
            "هر بار یک واحد بیشتر", "One more each time",
            "گام‌های ۱،۲،۳،۴،۵", "Steps 1,2,3,4,5")
    }

    private fun bossMixed(secId: String, day: Int, diff: Int): Level {
        val seq = listOf("A", "1", "B", "2", "C", "3")
        return lvl(secId, day, PuzzleType.MIXED, diff,
            seq.joinToString(", ") + ", ?", "D",
            "الگوی ترکیبی", "Mixed pattern",
            "حرف، عدد، حرف، عدد…", "Letter, number…",
            "به ترتیب پیش برو", "Follow the order",
            "تناوب حرف و عدد", "Alternating letter/number")
    }

    private fun generateLevel(secId: String, day: Int, mi: Int): Level = when (day) {
        1  -> arithInc(secId, day, 1 + mi * 2, 1, 1)
        2  -> arithInc(secId, day, 2 + mi, 2, 1)
        3  -> arithInc(secId, day, 5 + mi, 3, 2)
        4  -> lettersSkip(secId, day, 'A' + (mi % 4), 1, 2)
        5  -> arithDec(secId, day, 20 + mi * 3, 2, 2)
        6  -> arithInc(secId, day, 10 + mi, 10, 2)
        7  -> arithDec(secId, day, 100 + mi * 10, 7, 2)
        8  -> lettersSkip(secId, day, 'B' + (mi % 4), 1, 2)
        9  -> arithInc(secId, day, 3 + mi, 4, 2)
        10 -> letterNumber(secId, day, 1 + mi, 2)
        11 -> squares(secId, day, 1, 3)
        12 -> squares(secId, day, 2 + (mi % 2), 3)
        13 -> lettersSkip(secId, day, 'A', 2, 3)
        14 -> geo(secId, day, 2 + mi, 2, 3)
        15 -> alternating(secId, day, 5 + mi, 3, 1, 3)
        16 -> fib(secId, day, 1, 1, 4)
        17 -> triangular(secId, day, 1, 4)
        18 -> lettersSkip(secId, day, 'C' + (mi % 4), 2, 4)
        19 -> cubes(secId, day, 1, 4)
        20 -> letterNumber(secId, day, 5 + mi, 4)
        21 -> primes(secId, day, 0, 5)
        22 -> geo(secId, day, 1, 3, 5)
        23 -> lettersSkip(secId, day, 'Z' - (mi % 4), -2, 5)
        24 -> triangular(secId, day, 3 + mi, 5)
        25 -> twoStep(secId, day, 1 + mi, 5)
        26 -> twoStep(secId, day, mi, 5)
        27 -> primes(secId, day, 7, 5)
        28 -> fib(secId, day, 1 + mi, 2 + mi, 5)
        29 -> lettersSkip(secId, day, 'A', 3, 5)
        30 -> bossMixed(secId, day, 6)
        else -> arithInc(secId, day, 1, 1, 1)
    }
}

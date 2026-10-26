package ir.numdoz.data.seed

import ir.numdoz.data.model.Chapter
import ir.numdoz.data.model.Level
import ir.numdoz.data.model.PuzzleType
import ir.numdoz.data.model.Section
import ir.numdoz.data.model.Year
import ir.numdoz.i18n.toFaNum

object SeedData {

    val years: List<Year> = listOf(
        Year("arka", 0, "سال آرکا", "Year of Arka",
            "سفری میان چهار فصل ناشناخته",
            "A journey through four seasons",
            "#4FE3E0", "star", false),
        Year("lumena", 1, "سال لومِنا", "Year of Lumena",
            "سفری به سرزمین‌های نور و راز",
            "A journey through light and mystery",
            "#F59E0B", "brightness", true)
    )

    val chapters: List<Chapter> = listOf(
        Chapter("kalira", "arka", 0, "کالیرا", "Kalira",
            "سپیده‌دم و آغاز روشنایی", "Dawn and beginning of light",
            "#F59E0B", "sun", false),
        Chapter("soreva", "arka", 1, "سورِوا", "Soreva",
            "جریان و آرامش اقیانوس", "Ocean flow and calm",
            "#0EA5E9", "wave", false),
        Chapter("vexira", "arka", 2, "وکسیرا", "Vexira",
            "قدرت و گرمای آتش", "Power and heat of fire",
            "#EF4444", "flame", false),
        Chapter("nivala", "arka", 3, "نیوالا", "Nivala",
            "راز و سکون یخ", "Mystery and stillness of ice",
            "#A855F7", "snow", false)
    )

    val sections: List<Section> = listOf(
        Section("kalira_lumis",   "kalira", 0, "لومیس",   "Lumis",   0, "بیداری نور",           "Awakening of light",  "sunrise",    "#F59E0B"),
        Section("kalira_auriel",  "kalira", 1, "آئوریل",  "Auriel",  1, "طلوع طلایی",            "Golden dawn",         "brightness", "#FB923C"),
        Section("kalira_zephra",  "kalira", 2, "زِفرا",    "Zephra",  2, "نسیم شکوفه",            "Blossom breeze",      "wind",       "#FCD34D"),
        Section("soreva_tidalis", "soreva", 0, "تیدالیس", "Tidalis", 0, "نخستین موج",            "First wave",          "wave",       "#0EA5E9"),
        Section("soreva_aqualin", "soreva", 1, "آکوآلین", "Aqualin", 1, "ژرفای آب",              "Deep water",          "water",      "#0284C7"),
        Section("soreva_mistrel", "soreva", 2, "میسترِل",  "Mistrel", 2, "پرده‌های مه",             "Veils of mist",       "cloud",      "#7DD3FC"),
        Section("vexira_emberis", "vexira", 0, "امبریس",  "Emberis", 0, "اخگر و گرما",            "Ember and warmth",    "ember",      "#EF4444"),
        Section("vexira_pyrosa",  "vexira", 1, "پایروزا", "Pyrosa",  1, "شعله‌های سرکش",           "Wild flames",         "flame",      "#DC2626"),
        Section("vexira_solmar",  "vexira", 2, "سولمار",  "Solmar",  2, "غروب سرخ",               "Red sunset",          "sunset",     "#F97316"),
        Section("nivala_frostel", "nivala", 0, "فروستل",  "Frostel", 0, "نخستین شبنم یخ",         "First frost",         "snow",       "#A855F7"),
        Section("nivala_nyxara",  "nivala", 1, "نیکسارا", "Nyxara",  1, "آسمان شب",               "Night sky",           "moon",       "#8B5CF6"),
        Section("nivala_glacira", "nivala", 2, "گلاسیرا", "Glacira", 2, "یخچال ابدی",             "Eternal glacier",     "star",       "#C4B5FD")
    )

    val levels: List<Level> = buildList {
        sections.forEachIndexed { gi, sec ->
            for (day in 1..30) {
                try {
                    add(generateLevel(sec.id, day, gi))
                } catch (t: Throwable) {
                    println("[NumDoz/seed] FAILED gi=$gi day=$day: ${t.message}")
                    add(fallbackLevel(sec.id, day))
                }
            }
        }
        println("[NumDoz/seed] total ${size} levels")
    }

    private fun fallbackLevel(secId: String, day: Int) = Level(
        id = "${secId}_d${day.toString().padStart(2, '0')}",
        sectionId = secId, orderIndex = day - 1, dayNumber = day,
        titleFa = "دنباله", titleEn = "Sequence",
        puzzleType = PuzzleType.NUMBER, difficulty = 1,
        timeLimitSeconds = 60,
        sequence = "1, 2, 3, 4, ?", correctAnswer = "5",
        hint1Fa = "به الگو نگاه کن", hint1En = "Look at the pattern",
        hint2Fa = "هر بار ۱ واحد", hint2En = "+1 each time",
        explanationFa = "دنباله‌ی حسابی", explanationEn = "Arithmetic"
    )

    private fun generateLevel(secId: String, day: Int, gi: Int): Level {
        val m = gi + 1
        return when (day) {
            1  -> arith(secId, day, m, 1, 1)
            2  -> arith(secId, day, 2 * m, 2, 1)
            3  -> arith(secId, day, 3 * m + 1, 3, 2)
            4  -> arithDesc(secId, day, 100 + 10 * m, 5, 2)
            5  -> lettersSkip(secId, day, ('A' + (m - 1)), 1, 2)
            6  -> lettersSkip(secId, day, ('A' + (m - 1)), 2, 2)
            7  -> squares(secId, day, m, 2)
            8  -> squares(secId, day, 20 + m, 2)
            9  -> fib(secId, day, 1, 1 + m, 3)
            10 -> triangular(secId, day, m, 3)
            11 -> fib(secId, day, 2 + m, 3 + m, 4)
            12 -> cubes(secId, day, m, 4)
            13 -> geo(secId, day, m, 2, 3)
            14 -> geo(secId, day, 1 + m, 3, 3)
            15 -> pronic(secId, day, m, 3)
            16 -> primes(secId, day, m, 1, 4)
            17 -> primes(secId, day, m, 2, 4)
            18 -> alternating(secId, day, 10 + 2 * m, 3, 1, 4)
            19 -> letterNumber(secId, day, 1 + m, 3)
            20 -> twoStep(secId, day, 1 + m, 4)
            21 -> multiplyAdd(secId, day, 1 + m, 2, 1, 5)
            22 -> lookAndSay(secId, day, 1 + m, 6)
            23 -> recThree(secId, day, 1, 1, 1 + m, 6)
            24 -> catalan(secId, day, m, 6)
            25 -> descendingSquares(secId, day, 100 + m, 6)
            26 -> factorialDiff(secId, day, 1 + m, 7)
            27 -> keyboardLetters(secId, day, m, 7)
            28 -> monthLetters(secId, day, m, 7)
            29 -> ordinalsLetters(secId, day, m, 8)
            30 -> bossFinal(secId, day, m, 8)
            else -> arith(secId, day, 1, 1, 1)
        }
    }

    private fun lvl(secId: String, day: Int, type: PuzzleType, diff: Int,
                    seq: String, ans: String,
                    tFa: String, tEn: String, h1Fa: String, h1En: String,
                    h2Fa: String, h2En: String, eFa: String, eEn: String): Level = Level(
        id = "${secId}_d${day.toString().padStart(2, '0')}",
        sectionId = secId, orderIndex = day - 1, dayNumber = day,
        titleFa = tFa, titleEn = tEn, puzzleType = type, difficulty = diff,
        timeLimitSeconds = 60, sequence = seq, correctAnswer = ans,
        hint1Fa = h1Fa, hint1En = h1En, hint2Fa = h2Fa, hint2En = h2En,
        explanationFa = eFa, explanationEn = eEn
    )

    private fun arith(secId: String, day: Int, start: Int, step: Int, diff: Int): Level {
        val seq = (0 until 5).map { start + step * it }
        val ans = start + step * 5
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "دنباله‌ی افزایشی", "Ascending",
            "به اختلاف نگاه کن", "Look at differences",
            "هر بار ${toFaNum(step)} تا اضافه می‌شه", "Add $step each time",
            "حسابی با گام ${toFaNum(step)}", "Arithmetic, step $step")
    }

    private fun arithDesc(secId: String, day: Int, start: Int, step: Int, diff: Int): Level {
        val seq = (0 until 5).map { start - step * it }
        val ans = start - step * 5
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "دنباله‌ی کاهشی", "Descending",
            "هر عدد کوچک‌تر است", "Each is smaller",
            "هر بار ${toFaNum(step)} تا کم می‌شه", "Subtract $step",
            "نزولی با گام ${toFaNum(step)}", "Descending, step $step")
    }

    private fun geo(secId: String, day: Int, start: Int, ratio: Int, diff: Int): Level {
        val seq = mutableListOf(start)
        for (i in 1 until 4) seq += seq.last() * ratio
        val ans = seq.last() * ratio
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "دنباله‌ی هندسی", "Geometric",
            "هر عدد چند برابر قبلی؟", "How many times?",
            "هر بار در ${toFaNum(ratio)} ضرب", "Multiply by $ratio",
            "هندسی، نسبت ${toFaNum(ratio)}", "Geometric, ratio $ratio")
    }

    private fun squares(secId: String, day: Int, startN: Int, diff: Int): Level {
        val seq = (0 until 4).map { val v = startN + it; v * v }
        val last = startN + 4
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", (last * last).toString(),
            "مربع اعداد", "Squares",
            "۱×۱، ۲×۲، ۳×۳", "1×1, 2×2, 3×3",
            "توان دوم اعداد طبیعی", "Squares",
            "مربع ${toFaNum(startN)} تا ${toFaNum(last)}", "Squares $startN..$last")
    }

    private fun cubes(secId: String, day: Int, startN: Int, diff: Int): Level {
        val seq = (0 until 4).map { val v = startN + it; v * v * v }
        val last = startN + 4
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", (last * last * last).toString(),
            "مکعب اعداد", "Cubes",
            "توان سوم اعداد", "Cubes",
            "۱³، ۲³، ۳³، ۴³", "1³, 2³, 3³, 4³",
            "مکعب ${toFaNum(startN)} تا ${toFaNum(last)}", "Cubes $startN..$last")
    }

    private fun pronic(secId: String, day: Int, startN: Int, diff: Int): Level {
        val seq = (0 until 4).map { val v = startN + it; v * (v + 1) }
        val last = startN + 4
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", (last * (last + 1)).toString(),
            "اعداد پرونیک", "Pronic",
            "n × (n+1)", "n × (n+1)",
            "حاصل‌ضرب دو عدد متوالی", "Product of consecutive",
            "اعداد مستطیلی", "Rectangular")
    }

    private fun fib(secId: String, day: Int, a: Int, b: Int, diff: Int): Level {
        val seq = mutableListOf(a, b)
        for (i in 2 until 5) seq += seq[i - 1] + seq[i - 2]
        val ans = seq.last() + seq[seq.size - 2]
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "فیبوناچی", "Fibonacci",
            "جمع دو عدد قبلی؟", "Sum of previous two?",
            "هر عدد = دو عدد قبل", "Each = sum of two before",
            "دنباله‌ی فیبوناچی", "Fibonacci")
    }

    private fun triangular(secId: String, day: Int, start: Int, diff: Int): Level {
        val seq = mutableListOf(start)
        for (i in 1 until 5) seq += seq.last() + (start + i)
        val ans = seq.last() + (start + 5)
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "اعداد مثلثی", "Triangular",
            "به اختلاف‌ها نگاه کن", "Look at differences",
            "اختلاف‌ها یک واحد زیاد می‌شن", "Differences +1",
            "دنباله‌ی مثلثی", "Triangular")
    }

    private fun primes(secId: String, day: Int, startIdx: Int, stride: Int, diff: Int): Level {
        val primes = listOf(2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97)
        val seq = (0 until 4).map { primes[startIdx + stride * it] }
        val ans = primes[startIdx + stride * 4]
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "اعداد اول", "Primes",
            "بخش‌پذیری رو در نظر بگیر", "Divisibility",
            "اعداد اول با گام ${toFaNum(stride)}", "Primes with stride $stride",
            "دنباله‌ی اعداد اول", "Prime sequence")
    }

    private fun lettersSkip(secId: String, day: Int, start: Char, step: Int, diff: Int): Level {
        val seq = (0 until 4).map { (start + step * it).toString() }
        val ans = (start + step * 4).toString()
        return lvl(secId, day, PuzzleType.LETTER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "حروف با پرش", "Skipping letters",
            "به فاصله‌ی حروف نگاه کن", "Look at letter gaps",
            "پرش ${toFaNum(step)}", "Skip $step",
            "حروف با فاصله‌ی ${toFaNum(step)}", "Letters skip $step")
    }

    private fun letterNumber(secId: String, day: Int, startNum: Int, diff: Int): Level {
        val seq = (0 until 4).map { i -> val n = startNum + i; "${('A' + (n - 1))}$n" }
        val lastN = startNum + 4
        val ans = "${('A' + (lastN - 1))}$lastN"
        return lvl(secId, day, PuzzleType.MIXED, diff,
            seq.joinToString(", ") + ", ?", ans,
            "حرف و عدد", "Letter + Number",
            "هر حرف با یک عدد", "Each letter with a number",
            "حرف و عدد هم‌راستا", "Letter aligned with number",
            "ترکیب حرف و عدد", "Letter-number mix")
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
            "به جهت تغییرات نگاه کن", "Direction changes",
            "هر بار ${toFaNum(plusA)} زیاد و ${toFaNum(minusB)} کم", "+$plusA and −$minusB",
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

    private fun multiplyAdd(secId: String, day: Int, start: Int, k: Int, c: Int, diff: Int): Level {
        val seq = mutableListOf(start)
        for (i in 1 until 5) seq += seq.last() * k + c
        val ans = seq.last() * k + c
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "ضرب و جمع", "Multiply and add",
            "هر عدد = قبلی × ${toFaNum(k)} به‌علاوه‌ی ${toFaNum(c)}", "Each = prev × $k + $c",
            "فرمول: x × ${toFaNum(k)} + ${toFaNum(c)}", "x × $k + $c",
            "الگوی ضرب و جمع", "Multiply-add")
    }

    private fun lookAndSay(secId: String, day: Int, start: Int, diff: Int): Level {
        var current = start.toString()
        val seq = mutableListOf<String>()
        repeat(4) {
            seq += current
            val sb = StringBuilder()
            var i = 0
            while (i < current.length) {
                var cnt = 1
                while (i + cnt < current.length && current[i + cnt] == current[i]) cnt++
                sb.append(cnt).append(current[i])
                i += cnt
            }
            current = sb.toString()
        }
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", current,
            "بگو و بشمار", "Look-and-say",
            "هر جمله را با خواندن قبلی بساز", "Read previous aloud",
            "مثال: ${toFaNum(seq[0])} → ${toFaNum(seq[1])}", "Example: ${seq[0]} → ${seq[1]}",
            "دنباله‌ی look-and-say", "Look-and-say")
    }

    private fun recThree(secId: String, day: Int, a: Int, b: Int, c: Int, diff: Int): Level {
        val seq = mutableListOf(a, b, c)
        for (i in 3 until 5) seq += seq[i - 1] + seq[i - 2] + seq[i - 3]
        val ans = seq.last() + seq[seq.size - 2] + seq[seq.size - 3]
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans.toString(),
            "جمع سه جمله", "Sum of three",
            "جمع سه عدد قبلی", "Sum of three previous",
            "aₙ = aₙ₋₁ + aₙ₋₂ + aₙ₋₃", "aₙ = aₙ₋₁ + aₙ₋₂ + aₙ₋₃",
            "بازگشتی سه‌جمله‌ای", "Three-term recursion")
    }

    private fun catalan(secId: String, day: Int, startIdx: Int, diff: Int): Level {
        fun cat(n: Int): Long {
            if (n <= 0) return 1L
            var r = 1L
            for (i in 1..n) r = r * (4L * i - 2L) / (i + 1)
            return r
        }
        val start = startIdx.coerceIn(0, 25)
        val seq = (0 until 4).map { cat(start + it).toString() }
        val ans = cat(start + 4).toString()
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "اعداد کاتالان", "Catalan",
            "Cₙ = Σ Cᵢ·Cₙ₋₁₋ᵢ", "Cₙ = Σ Cᵢ·Cₙ₋₁₋ᵢ",
            "۱، ۲، ۵، ۱۴، ۴۲...", "1, 2, 5, 14, 42...",
            "دنباله‌ی کاتالان", "Catalan sequence")
    }

    private fun descendingSquares(secId: String, day: Int, startN: Int, diff: Int): Level {
        val seq = (0 until 4).map { val v = startN - it; v * v }
        val last = startN - 4
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", (last * last).toString(),
            "مربع نزولی", "Descending squares",
            "به ترتیب نزولی", "Descending order",
            "${toFaNum(startN)}²، ${toFaNum(startN - 1)}²، ${toFaNum(startN - 2)}²", "${startN}², ${startN - 1}², ${startN - 2}²",
            "مربع نزولی", "Descending squares")
    }

    private fun factorialDiff(secId: String, day: Int, startN: Int, diff: Int): Level {
        val seq = mutableListOf(0L)
        var fact = 1L
        for (i in 1 until 5) { fact *= i; seq += seq.last() + fact }
        val ans = (seq.last() + fact * 5).toString()
        return lvl(secId, day, PuzzleType.NUMBER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "تفاضل فاکتوریل", "Factorial differences",
            "اختلاف‌ها: ۱، ۲، ۶، ۲۴", "Differences: 1, 2, 6, 24",
            "اختلاف‌ها = n!", "Differences = n!",
            "جمع فاکتوریل‌ها", "Sum of factorials")
    }

    private fun keyboardLetters(secId: String, day: Int, offset: Int, diff: Int): Level {
        val kb = listOf('Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M')
        val start = (offset - 1) % (kb.size - 4)
        val seq = (0 until 4).map { kb[start + it].toString() }
        val ans = kb[start + 4].toString()
        return lvl(secId, day, PuzzleType.LETTER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "حروف کیبورد", "Keyboard letters",
            "ترتیب کیبورد", "Keyboard layout",
            "${seq[0]} ${seq[1]} ${seq[2]} ${seq[3]}...", "${seq[0]} ${seq[1]} ${seq[2]} ${seq[3]}...",
            "حروف کیبورد", "Keyboard letters")
    }

    private fun monthLetters(secId: String, day: Int, offset: Int, diff: Int): Level {
        val months = listOf('J','F','M','A','M','J','J','A','S','O','N','D')
        val start = (offset - 1) % (months.size - 4)
        val seq = (0 until 4).map { months[start + it].toString() }
        val ans = months[start + 4].toString()
        return lvl(secId, day, PuzzleType.LETTER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "حروف ماه‌ها", "Month letters",
            "حرف اول ماه‌های میلادی", "First letters of months",
            "${seq[0]} ${seq[1]} ${seq[2]} ${seq[3]}...", "${seq[0]} ${seq[1]} ${seq[2]} ${seq[3]}...",
            "حروف اول ماه", "First letters")
    }

    private fun ordinalsLetters(secId: String, day: Int, offset: Int, diff: Int): Level {
        val ord = listOf('F','S','T','F','F','S','S','E','N','T')
        val start = (offset - 1) % (ord.size - 4)
        val seq = (0 until 4).map { ord[start + it].toString() }
        val ans = ord[start + 4].toString()
        return lvl(secId, day, PuzzleType.LETTER, diff,
            seq.joinToString(", ") + ", ?", ans,
            "حروف ترتیبی", "Ordinal letters",
            "حرف اول اعداد ترتیبی", "First letter of ordinals",
            "first→F، second→S، third→T", "first→F, second→S, third→T",
            "حروف اعداد ترتیبی", "Ordinal letters")
    }

    private fun bossFinal(secId: String, day: Int, m: Int, diff: Int): Level {
        val seq = listOf(
            "${('A' + m - 1)}${m}",
            "${('B' + m - 1)}${m * 2}",
            "${('C' + m - 1)}${m * 2 + 3}",
            "${('D' + m - 1)}${m * 2 + 6}"
        )
        val ans = "${('E' + m - 1)}${m * 2 + 9}"
        return lvl(secId, day, PuzzleType.MIXED, diff,
            seq.joinToString(", ") + ", ?", ans,
            "الگوی ترکیبی نهایی", "Final mixed",
            "حرف و عدد را جدا حساب کن", "Letter and number separately",
            "حرف: هر بار ۱ جلوتر؛ عدد: اول ${toFaNum(m)} بعد ۳", "Letter: +1; Number: +$m then +3",
            "ترکیب حرف و عدد", "Mixed letter-number")
    }
}

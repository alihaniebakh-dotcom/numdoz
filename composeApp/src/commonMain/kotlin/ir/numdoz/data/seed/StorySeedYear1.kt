package ir.numdoz.data.seed

import ir.numdoz.data.model.StoryChapter

object StorySeedYear1 {

    private const val YEAR = "arka"

    val stories: List<StoryChapter> = listOf(

        StoryChapter(
            id = "story_arka_intro",
            yearId = YEAR,
            position = 0,
            titleFa = "بیداری",
            titleEn = "Awakening",
            bodyFa = "در تاریکی بیدار می‌شوی. نه اسمی یادت هست، نه جایی که از آن آمده‌ای. فقط یک حس عجیب: باید اعداد را بچینی.\n\nآسمان پر از ستاره‌های عددی است — هر کدام یک نور. صدایی از همه‌جا می‌آید: «به عددچین خوش آمدی. برای بازگشت، از چهار دروازه بگذر: نور، آب، آتش، راز. هر عددی که بچینی، یک پاره از خودت را برمی‌گردانی.»\n\nاولین عدد را از زمین برمی‌داری. گرم است. سفرت آغاز می‌شود.",
            bodyEn = "You wake in the dark. No name. No memory. Only one strange feeling: you must arrange the numbers.\n\nThe sky is full of numbered stars — each one a light. A voice from everywhere: \"Welcome to NumDoz. To return, pass through four gates: Light, Water, Fire, Mystery. Every number you arrange returns a piece of yourself.\"\n\nYou pick up the first number. It is warm. Your journey begins.",
            accentColorHex = "#4FE3E0",
            logoName = "star"
        ),

        StoryChapter(
            id = "story_arka_m01",
            yearId = YEAR,
            position = 1,
            titleFa = "لومین",
            titleEn = "Lumin",
            bodyFa = "دروازه‌ی نور باز می‌شود. درختی عظیم در مرکز، برگ‌هایش همه عدد. نگهبانی پیر با عصای درخشان: «نام من لومین است. هر معمایی که حل کنی، یک برگ از درخت می‌افتد و در دستت می‌نشیند.»\n\nاولین برگ را می‌گیری. رویش نوشته: «آرکا». زیر لب تکرار می‌کنی — آرکا. انگار این کلمه را از قدیم می‌شناسی.\n\n«ادامه بده، آرکا — خاطرات بیشتری در انتظارند.»",
            bodyEn = "The gate of Light opens. A vast tree in the center, its leaves all numbers. An old guardian: \"I am Lumin. Every puzzle you solve, a leaf falls into your hand.\"\n\nYou take the first leaf. It says: \"Arka.\" You whisper it — Arka. As if you've always known this word.\n\n\"Continue, Arka — more memories await.\"",
            accentColorHex = "#F59E0B",
            logoName = "sunrise"
        ),

        StoryChapter(
            id = "story_arka_m02",
            yearId = YEAR,
            position = 2,
            titleFa = "خورشید دو نیم",
            titleEn = "Split Sun",
            bodyFa = "خورشید در آسمان دو نیم است. نیمی طلایی، نیمی سیاه. لومین: «انتخاب کن، آرکا. نمی‌توانی هر دو را داشته باشی.»\n\nبه سیاهی نگاه می‌کنی. سایه‌ای از خودت — خسته، ترسان، تنها. دستت را به سمت سیاهی می‌بری. سیاهی به نور تبدیل می‌شود.\n\nلومین لبخند می‌زند: «درست انتخاب کردی. نور واقعی از پذیرش تاریکی می‌آید.»",
            bodyEn = "The sun is split in two. Half golden, half black. Lumin: \"Choose, Arka. You cannot have both.\"\n\nYou look into the dark. A shadow of yourself — tired, afraid, alone. You reach toward the dark. The dark becomes light.\n\nLumin smiles: \"You chose well. True light comes from embracing the darkness.\"",
            accentColorHex = "#FB923C",
            logoName = "brightness"
        ),

        StoryChapter(
            id = "story_arka_m03",
            yearId = YEAR,
            position = 3,
            titleFa = "دروازه‌ی آب",
            titleEn = "Gate of Water",
            bodyFa = "قلمروی نور به پایان می‌رسد. لومین تو را تا دروازه‌ی بعدی همراهی می‌کند: «قلمروی آب سردتر است. آینه‌ها حقیقت را نشان می‌دهند. اگر گم شدی، به بازتاب خودت نگاه کن.»\n\nدروازه‌ای از آب زلال باز می‌شود. قدم به آب می‌گذاری. سرد نیست — گرم است، مثل خاطره‌ای دور.",
            bodyEn = "The realm of Light is over. Lumin walks you to the gate: \"Water is colder. Mirrors show the truth. If you get lost, look at your reflection.\"\n\nA gate of clear water opens. You step in. It isn't cold — it's warm, like a distant memory.",
            accentColorHex = "#FCD34D",
            logoName = "wind"
        ),

        StoryChapter(
            id = "story_arka_m04",
            yearId = YEAR,
            position = 4,
            titleFa = "آینه",
            titleEn = "The Mirror",
            bodyFa = "در جویباری بی‌پایان گم می‌شوی. آب تصویرت را نشان می‌دهد — ولی چهره‌ای که می‌بینی تو نیست. پسری با لباس‌های دنیای قدیم.\n\nمی‌شناسی‌اش: خودت بودی، قبل از شکستن. آب می‌گوید: «این تو بودی. قبل از شکستن.»\n\nقطره‌ای روی گونه‌ات — نه، اشک است. صدایی از عمق: «حافظه‌ات برمی‌گردد، قطره قطره.»",
            bodyEn = "You get lost in an endless stream. The water shows your reflection — but it's not you. A boy in old-world clothes.\n\nYou recognize him: you, before the breaking. The water says: \"This was you. Before the breaking.\"\n\nA drop on your cheek — no, a tear. A voice: \"Your memory returns, drop by drop.\"",
            accentColorHex = "#0EA5E9",
            logoName = "wave"
        ),

        StoryChapter(
            id = "story_arka_m05",
            yearId = YEAR,
            position = 5,
            titleFa = "موج",
            titleEn = "The Wave",
            bodyFa = "موجی عظیم به سمتت می‌آید. الگوی اعداد موج را می‌بینی — پیچیده، غیرقابل پیش‌بینی. برای نجات باید الگو را از نو بسازی.\n\nدستت در هوا می‌رقصد. آخرین عدد. موج متوقف می‌شود، آرام می‌شود، راه باز می‌کند.\n\nصدایی: «کسی که این موج را ساخت، خودت بودی. آیا یادت هست؟»",
            bodyEn = "A giant wave rushes at you. Its number-pattern is complex, unpredictable. To survive, you must rebuild it.\n\nYour fingers dance in the air. The last number. The wave stops, softens, opens a path.\n\nA voice: \"The one who built this wave was you. Do you remember?\"",
            accentColorHex = "#0284C7",
            logoName = "water"
        ),

        StoryChapter(
            id = "story_arka_m06",
            yearId = YEAR,
            position = 6,
            titleFa = "جاده‌ی سنگی",
            titleEn = "Stone Road",
            bodyFa = "دیا آرام می‌شود، مه کنار می‌رود. زنی با موهای آبی — نگهبان آب — روی سنگی نشسته: «آتش تو را نمی‌سوزاند، اگر خودت را بشناسی.»\n\n«خودم را می‌شناسم؟»\n\n«نه. حالا فقط اسمت را می‌دانی.»\n\nجاده‌ای سنگی به سمت آتش. آتش انتظار دارد، ولی آتش صادق‌تر از آب است.",
            bodyEn = "The sea calms, the mist lifts. A woman with blue hair — guardian of Water: \"Fire won't burn you, if you know yourself.\"\n\n\"Do I know myself?\"\n\n\"No. You only know your name.\"\n\nA stone road leads toward fire. Fire waits, but fire is more honest than water.",
            accentColorHex = "#7DD3FC",
            logoName = "cloud"
        ),

        StoryChapter(
            id = "story_arka_m07",
            yearId = YEAR,
            position = 7,
            titleFa = "چرا اینجایی؟",
            titleEn = "Why Are You Here?",
            bodyFa = "دشتستانی از آتش. موجودی بی‌شکل از شعله: «چرا اینجایی، آرکا؟»\n\n«تا برگردم.»\n\n«به کجا؟»\n\nسکوت. جوابی نداری. اسمت را می‌دانی، تصویرت را دیده‌ای، ولی مقصد را نه.\n\nنگهبان: «جواب را پیدا کن. تا وقتی مقصد را نشناسی، از این آتش بیرون نمی‌روی.»",
            bodyEn = "A plain of fire. A shapeless creature of flame: \"Why are you here, Arka?\"\n\n\"To go back.\"\n\n\"Back where?\"\n\nSilence. You have no answer. You know your name, you've seen your face, but not your destination.\n\n\"Find the answer. Until you know where you're going, this fire won't let you out.\"",
            accentColorHex = "#EF4444",
            logoName = "ember"
        ),

        StoryChapter(
            id = "story_arka_m08",
            yearId = YEAR,
            position = 8,
            titleFa = "کلمات شعله",
            titleEn = "Words of Flame",
            bodyFa = "شعله‌های بزرگ شکل کلمات می‌سازند: «تو» / «در» / «شکستی».\n\nروی زانو می‌افتی. یادت می‌آید — در دنیای قدیم، در جستجوی قدرت بودی. دروازه‌های قلمروها را شکستی تا قدرت را لمس کنی.\n\nنگهبان: «تنها راه ترمیم، قلمروی راز است — و بخشیدن خودت.»",
            bodyEn = "Great flames form words: \"You\" / \"Broke\" / \"The gates\".\n\nYou fall to your knees. You remember — in the old world, you sought power. You broke the gates to touch it.\n\n\"The only way to heal is through the realm of Mystery — and forgiving yourself.\"",
            accentColorHex = "#DC2626",
            logoName = "flame"
        ),

        StoryChapter(
            id = "story_arka_m09",
            yearId = YEAR,
            position = 9,
            titleFa = "خاکستر",
            titleEn = "Ashes",
            bodyFa = "آتشفشان فوران می‌کند. خاکستر تو را آرام می‌کند. هر ذره، یک اشتباه قدیمی که سبک می‌شود.\n\nنگهبان: «یک قدم مانده. نیمی از تو در تاریکی ماند — همان‌وقت که دروازه را شکستی.»\n\nدروازه‌ای از دود باز می‌شود. صدای خودت: «آرکا… بالاخره آمدی.»",
            bodyEn = "The volcano erupts. The ash calms you. Each grain, an old mistake becoming lighter.\n\n\"One step left. Half of you stayed in the dark — when you broke the gate.\"\n\nA gate of smoke opens. Your own voice: \"Arka… you finally came.\"",
            accentColorHex = "#F97316",
            logoName = "sunset"
        ),

        StoryChapter(
            id = "story_arka_m10",
            yearId = YEAR,
            position = 10,
            titleFa = "سایه",
            titleEn = "The Shadow",
            bodyFa = "شب قطبی. سایه‌ای آشنا: «من تو هستم. نسخه‌ای که در تاریکی ماند. نسخه‌ای که هیچ‌وقت نبخشیدی.»\n\n«چرا برنگشتی؟»\n\n«چون تو برنگشتی. برای برگشت، باید اول من را قبول کنی.»\n\nدستت را دراز می‌کنی. سایه دستت را می‌گیرد. برای اولین بار، سبک می‌شوی.",
            bodyEn = "Polar night. A familiar shadow: \"I am you. The version left in the dark. The version you never forgave.\"\n\n\"Why didn't you return?\"\n\n\"Because you didn't. To return, you must first accept me.\"\n\nYou reach out. Your shadow takes your hand. For the first time, you feel light.",
            accentColorHex = "#A855F7",
            logoName = "snow"
        ),

        StoryChapter(
            id = "story_arka_m11",
            yearId = YEAR,
            position = 11,
            titleFa = "کهکشان",
            titleEn = "Galaxy",
            bodyFa = "کهکشان‌ها به دورت می‌چرخند. هر ستاره یک خاطره.\n\nسایه‌ات کنارت: «این‌ها را ساختی. از هیچ.»\n\n«با کمک تو.»\n\n«با کمک خودت. من فقط یادت انداختم تنها نیستی.»\n\nستاره‌ها به دروازه‌ی نوری تبدیل می‌شوند. «قدم آخر. سیاه‌چاله.»",
            bodyEn = "Galaxies orbit you. Every star a memory.\n\nYour shadow beside you: \"You built these. From nothing.\"\n\n\"With your help.\"\n\n\"With your own. I only reminded you you're not alone.\"\n\nThe stars turn into a gate of light. \"Last step. The black hole.\"",
            accentColorHex = "#8B5CF6",
            logoName = "moon"
        ),

        StoryChapter(
            id = "story_arka_m12",
            yearId = YEAR,
            position = 12,
            titleFa = "بازگشت",
            titleEn = "Return",
            bodyFa = "سیاه‌چاله در مرکز. صدای خودت: «اگر وارد شوی، دیگر برنمی‌گردی. ولی می‌توانی دنیای جدیدی بسازی.»\n\nبه پشت سر نگاه می‌کنی. ۳۶۰ عدد، ۳۶۰ خاطره.\n\nقدم اول. قدم دوم. سیاه‌چاله به نور تبدیل می‌شود.\n\nدر قلب نوری که خودت ساختی ایستاده‌ای. سایه‌ات کنارت — این بار در نور. «حالا می‌دانی عددچین کیست.»\n\n«آرکا.»\n\n«نه. عددچین. کسی که اعداد را می‌چیند تا دنیا را بسازد.»\n\nبه افق نگاه می‌کنی. سال دوم در راه است.",
            bodyEn = "The black hole at the center. Your own voice: \"If you enter, you can't return. But you can build a new world.\"\n\nYou look behind. 360 numbers, 360 memories.\n\nFirst step. Second step. The black hole turns to light.\n\nYou stand in the heart of the light you built. Your shadow beside you — this time in the light. \"Now you know who NumDoz is.\"\n\n\"Arka.\"\n\n\"No. NumDoz. The one who arranges numbers to build worlds.\"\n\nYou look at the horizon. Year Two is coming.",
            accentColorHex = "#C4B5FD",
            logoName = "star"
        )
    )
}
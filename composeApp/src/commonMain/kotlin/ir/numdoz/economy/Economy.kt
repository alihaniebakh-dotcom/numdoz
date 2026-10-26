package ir.numdoz.economy

/**
 * Coin economy constants — single source of truth.
 */
object Economy {
    const val COINS_PER_AD = 10
    const val COINS_PER_DAILY = 20
    const val COINS_PER_LEVEL_COMPLETE = 3
    const val COINS_PER_INTERSTITIAL = 3

    const val HINT_PRICE = 15
    const val TIME_BOOST_PRICE = 15
    const val EXTRA_STAR_PRICE = 35
    const val SKIP_LEVEL_PRICE = 40
    const val EDIT_PROFILE_PRICE = 25

    /** Coins awarded per session based on tracking — cleanly formatted. */
    fun coinMultiplierForStreak(dayStreak: Int): Int = when {
        dayStreak >= 30 -> 3
        dayStreak >= 14 -> 2
        dayStreak >= 7 -> 1
        else -> 1
    }
}

package ir.numdoz.util
actual fun currentTimeMillis(): Long = System.currentTimeMillis()
actual fun localDayKey(ms: Long): Long {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = ms
    return cal.get(java.util.Calendar.YEAR) * 1000L + cal.get(java.util.Calendar.DAY_OF_YEAR)
}

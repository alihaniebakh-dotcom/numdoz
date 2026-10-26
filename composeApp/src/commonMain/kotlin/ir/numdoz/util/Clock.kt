package ir.numdoz.util

expect fun currentTimeMillis(): Long
expect fun localDayKey(ms: Long): Long

fun isSameLocalDay(a: Long, b: Long): Boolean = a > 0 && localDayKey(a) == localDayKey(b)

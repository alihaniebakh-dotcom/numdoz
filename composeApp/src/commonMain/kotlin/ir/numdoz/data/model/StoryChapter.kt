package ir.numdoz.data.model

/**
 * A narrative chapter.
 * position = 0 → intro (before Month 1)
 * position 1-11 → after Month N
 * position 12 → end of year (after Month 12)
 */
data class StoryChapter(
    val id: String,
    val yearId: String,
    val position: Int,
    val titleFa: String,
    val titleEn: String,
    val bodyFa: String,
    val bodyEn: String,
    val accentColorHex: String,
    val logoName: String
)
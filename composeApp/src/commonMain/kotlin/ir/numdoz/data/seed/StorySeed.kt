package ir.numdoz.data.seed

import ir.numdoz.data.model.StoryChapter

object StorySeed {

    val all: List<StoryChapter> = StorySeedYear1.stories + StorySeedYear2.stories

    fun introFor(yearId: String): StoryChapter? =
        all.firstOrNull { it.yearId == yearId && it.position == 0 }

    fun afterMonth(yearId: String, position: Int): StoryChapter? =
        all.firstOrNull { it.yearId == yearId && it.position == position }
}
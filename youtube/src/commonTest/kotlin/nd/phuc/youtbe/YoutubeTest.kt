package nd.phuc.youtbe

import kotlinx.coroutines.test.runTest
import nd.phuc.youtube.YouTube
import kotlin.test.Test

class YoutubeTest {
    @Test
    fun testSearch() = runTest {
        val result = YouTube.search(
            query = "Sự thật sau một lời hứa remix",
            filter = YouTube.SearchFilter.FILTER_VIDEO,
        )
        println(result)
    }
}

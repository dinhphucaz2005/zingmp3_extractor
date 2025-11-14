package nd.phuc.youtube.export

import kotlinx.serialization.json.Json
import nd.phuc.youtube.YouTube
import nd.phuc.youtube.platform.createClient
import nd.phuc.youtube.platform.getHomeDirectory
import okio.FileSystem
import okio.Path.Companion.toPath

suspend fun search(
    query: String,
) {

    val result = YouTube.search(
        query = query,
        filter = YouTube.SearchFilter.FILTER_VIDEO,
    ).getOrNull() ?: return

    val items = result.items.map {
        SearchSummaryResult(
            id = it.id,
            title = it.title,
            cover = it.thumbnail,
        )
    }

    val home = getHomeDirectory()
    val outputDir = "$home/Music/BeatShell".toPath()
    val jsonDir = outputDir / "metadata"

    FileSystem.SYSTEM.createDirectories(outputDir)
    FileSystem.SYSTEM.createDirectories(jsonDir)

    val client = createClient {}

    for (item in items) {
        val cover = item.cover
        if (cover.isNullOrEmpty()) continue

        val outPath = outputDir / "${item.id}.jpg"
        val jsonPath = jsonDir / "${item.title.sanitizeFileName()}.json"
        createCover(url = cover, outputPath = outPath.toString())
        item.cover = outPath.toString()

        val jsonStr = Json.encodeToString(item)
        FileSystem.SYSTEM.write(jsonPath) {
            writeUtf8(jsonStr)
        }
        println(jsonPath)
    }

    client.close()
}

private fun String?.sanitizeFileName(): String? {
    if (this == null) return null
    return replace(Regex("[\\\\/:*?\"<>|]"), "_")
}

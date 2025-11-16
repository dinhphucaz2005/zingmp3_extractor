package nd.phuc.youtube

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import nd.phuc.youtube.export.SearchSummaryResult
import nd.phuc.youtube.platform.createClient
import nd.phuc.youtube.platform.getHomeDirectory
import okio.FileSystem
import okio.Path.Companion.toPath

suspend fun search(
    query: String,
    visitorData: String? = null,
    cookie: String? = null,
) {
    visitorData?.let { YouTube.visitorData = it }
    cookie?.let { YouTube.cookie = it }

    val result = YouTube.search(
        query = query,
        filter = YouTube.SearchFilter.FILTER_VIDEO,
    ).getOrNull() ?: return

    val items = result.items.map {
        SearchSummaryResult(
            id = it.id,
            title = it.title,
            thumbnail = it.thumbnail,
        )
    }

    val home = getHomeDirectory()
    val outputDir = "$home/Music/BeatShell".toPath()
    val jsonDir = outputDir / "metadata"

    FileSystem.SYSTEM.createDirectories(outputDir)
    FileSystem.SYSTEM.createDirectories(jsonDir)

    val client = createClient {}

    for (item in items) {
        val cover = item.thumbnail
        if (cover.isNullOrEmpty()) continue

        val outPath = outputDir / "${item.id}.jpg"
        val jsonPath = jsonDir / "${item.title.sanitizeFileName()}.json"

        if (!FileSystem.SYSTEM.exists(outPath)) {
            try {
                val response: HttpResponse = client.get(cover)
                val bytes = response.body<ByteArray>()
                FileSystem.SYSTEM.write(outPath) {
                    write(bytes)
                }
            } catch (_: Exception) {
                continue
            }
        }

        item.thumbnail = outPath.toString()

        val jsonStr = Json.encodeToString(item)
        FileSystem.SYSTEM.write(jsonPath) {
            writeUtf8(jsonStr)
        }
        println(jsonPath)
    }

    client.close()
}

suspend fun playlist(
    playlistId: String,
    visitorData: String? = null,
    cookie: String? = null,
) {
    visitorData?.let { YouTube.visitorData = it }
    cookie?.let { YouTube.cookie = it }

    val result = YouTube.playlist(
        playlistId = playlistId,
    ).getOrNull() ?: return

    println("Playlist: ${result}")
}

private fun String?.sanitizeFileName(): String? {
    if (this == null) return null
    return replace(Regex("[\\\\/:*?\"<>|]"), "_")
}

fun main(args: Array<String>) = runBlocking {
    playlist(
        playlistId = "RDyssR2xaeTGU",
    )
//    if (args.size < 2 || args[0] != "--search") {
//        println("Usage: <program> --search <query>")
//        return@runBlocking
//    }
//    search(args[1])
}

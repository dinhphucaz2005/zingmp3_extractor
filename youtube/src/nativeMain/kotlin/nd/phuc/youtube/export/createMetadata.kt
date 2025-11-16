package nd.phuc.youtube.export

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import nd.phuc.youtube.models.YTItem
import nd.phuc.youtube.platform.eprintln
import nd.phuc.youtube.platform.mkdir
import okio.FileSystem
import okio.Path.Companion.toPath

suspend fun createMetadata(ytItem: YTItem, type: Type? = null, prefix: String = "/"): String? {
    if (ytItem.thumbnail.isEmpty()) return null

    val coverPath = Config.appDirectory.toPath() / Type.THUMBNAILS.dir / "${ytItem.id}.jpg"
    mkdir(coverPath.parent.toString())

    val metadataPath = Config.appDirectory.toPath() / prefix.dropWhile { it == '/' } / (type?.dir
        ?: "") / "${ytItem.title.sanitizeFileName()}.json"
    mkdir(metadataPath.parent.toString())


    try {
        val response: HttpResponse = Config.httpClient.get(ytItem.thumbnail)
        val bytes = response.body<ByteArray>()
        FileSystem.SYSTEM.write(coverPath) {
            write(bytes)
        }
    } catch (e: Exception) {
        eprintln(message = "Failed to download thumbnail for ${ytItem.title} - ${e.message}")
    }

    val jsonStr = Json.encodeToString(
        Metadata(
            id = ytItem.id,
            title = ytItem.title,
            thumbnail = coverPath.toString(),
        )
    )
    FileSystem.SYSTEM.write(metadataPath) {
        writeUtf8(jsonStr)
    }
    return metadataPath.toString()
}
package nd.phuc.youtube.export

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import nd.phuc.youtube.YouTube
import nd.phuc.youtube.platform.createClient
import nd.phuc.youtube.platform.getHomeDirectory
import okio.FileSystem
import okio.Path.Companion.toPath

suspend fun createCover(url: String, outputPath: String) {
    val client = createClient {}
    try {
        val response: HttpResponse = client.get(url)
        val bytes = response.body<ByteArray>()
        FileSystem.SYSTEM.write(outputPath.toPath()) {
            write(bytes)
        }
    } catch (_: Exception) {
    } finally {
        client.close()
    }
}
package nd.phuc.music.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nd.phuc.music.common.data.Song
import java.io.File

private fun getUserDirectory(): String {
    return System.getProperty("user.home") ?: "."
}

actual fun getLocalSongs(): List<Song> {
    val directory = getUserDirectory() + "/Music"
    val musicFiles = File(directory).listFiles { file ->
        val name = file.name.lowercase()
        file.isFile && (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".flac"))
    } ?: return emptyList()
    return musicFiles.map { file ->
        Song(
            id = file.absolutePath.hashCode().toString(),
            title = file.nameWithoutExtension,
            source = Song.Source.LocalFile(file.absolutePath),
            artist = "Unknown Artist",
        )
    }
}

actual suspend fun getThumbnailForSong(song: Song): File {
    return withContext(Dispatchers.IO) {
        when (song.source) {
            is Song.Source.LocalFile -> {
                fun getCacheDirectory(): String {
                    val cacheDir = File("${getUserDirectory()}/.caches")
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs()
                    }
                    return cacheDir.absolutePath
                }

                val ffmpegBinary = "ffmpeg"
                val outputPath = "${getCacheDirectory()}/${song.source.path.hashCode()}.png"

                if (File(outputPath).exists()) return@withContext File(outputPath)

                val processBuilder = ProcessBuilder(
                    ffmpegBinary,
                    "-i", song.source.path,
                    outputPath,
                )

                processBuilder.redirectErrorStream(true)
                val process = processBuilder.start()

                val result = process.inputStream.bufferedReader().readText()
                val exitCode = process.waitFor()

                if (exitCode == 0) {
                    println("Thumbnail created at $outputPath")
                } else {
                    println("FFmpeg failed:\n$result")
                }
                return@withContext File(outputPath)
            }

            is Song.Source.RemoteUrl -> throw NotImplementedError("RemoteUrl thumbnail extraction not implemented yet.")
            is Song.Source.UriFile -> throw NotImplementedError("UriFile thumbnail extraction not implemented yet.")
        }
    }
}

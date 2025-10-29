package nd.phuc.music.common.data

import nd.phuc.music.platform.getThumbnailForSong
import java.io.File

data class Song(
    val id: String,
    val source: Source,
    val title: String,
    val artist: String,
    val duration: String = "3:45"
) {

    suspend fun getFileThumbnail(): File = getThumbnailForSong(song = this)

    sealed class Source {
        data class LocalFile(val path: String) : Source()
        data class UriFile(val uri: String) : Source()
        data class RemoteUrl(val url: String) : Source()
    }
}
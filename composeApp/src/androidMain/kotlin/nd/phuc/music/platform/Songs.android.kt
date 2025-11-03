package nd.phuc.music.platform

import nd.phuc.music.domain.model.Song
import java.io.File
import java.util.UUID


actual fun getLocalSongs(): List<Song> {
    throw NotImplementedError("getLocalSongs is not implemented for Android yet.")
}

actual suspend fun getThumbnailForSong(song: Song): File {
    TODO("Not yet implemented")
}
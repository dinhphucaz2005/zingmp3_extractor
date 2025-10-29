package nd.phuc.music.platform

import nd.phuc.music.common.data.Song
import java.io.File
import java.util.UUID


actual fun getLocalSongs(): List<Song> {
    return listOf(
        Song(
            id = UUID.randomUUID().toString(),
            source = Song.Source.LocalFile("/path/to/local/song.mp3"),
            title = "Local Song",
            artist = "Local Artist",
            duration = "240000",
        ), Song(
            id = UUID.randomUUID().toString(),
            source = Song.Source.LocalFile("/path/to/local/song.mp3"),
            title = "Local Song",
            artist = "Local Artist",
            duration = "240000",
        ), Song(
            id = UUID.randomUUID().toString(),
            source = Song.Source.LocalFile("/path/to/local/song.mp3"),
            title = "Local Song",
            artist = "Local Artist",
            duration = "240000",
        ), Song(
            id = UUID.randomUUID().toString(),
            source = Song.Source.LocalFile("/path/to/local/song.mp3"),
            title = "Local Song",
            artist = "Local Artist",
            duration = "240000",
        ), Song(
            id = UUID.randomUUID().toString(),
            source = Song.Source.LocalFile("/path/to/local/song.mp3"),
            title = "Local Song",
            artist = "Local Artist",
            duration = "240000",
        )
    )
    throw NotImplementedError("getLocalSongs is not implemented for Android yet.")
}

actual suspend fun getThumbnailForSong(song: Song): File {
    TODO("Not yet implemented")
}
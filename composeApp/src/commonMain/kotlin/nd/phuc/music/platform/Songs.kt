package nd.phuc.music.platform

import nd.phuc.music.domain.model.Song
import java.io.File


expect fun getLocalSongs(): List<Song>

expect suspend fun getThumbnailForSong(song: Song): File
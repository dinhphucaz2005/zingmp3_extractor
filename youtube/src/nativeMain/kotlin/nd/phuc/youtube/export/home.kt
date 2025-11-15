package nd.phuc.youtube.export

import nd.phuc.youtube.YouTube
import nd.phuc.youtube.models.AlbumItem
import nd.phuc.youtube.models.ArtistItem
import nd.phuc.youtube.models.PlaylistItem
import nd.phuc.youtube.models.SongItem
import kotlin.collections.forEach

suspend fun home() {

    val result = YouTube.home().getOrNull() ?: throw Exception("Failed to get home info")

    result.sections.forEach { section ->
        section.items.forEach { item ->
            println(
                createMetadata(
                    ytItem = item, type = when (item) {
                        is AlbumItem -> Type.ALBUM
                        is ArtistItem -> Type.ARTIST
                        is PlaylistItem -> Type.PLAYLIST
                        is SongItem -> Type.VIDEO
                    },
                    prefix = "home"
                )
            )
        }
    }

}
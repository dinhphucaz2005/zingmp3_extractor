package nd.phuc.youtube.pages

import nd.phuc.youtube.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)

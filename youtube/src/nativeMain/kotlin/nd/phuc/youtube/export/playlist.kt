package nd.phuc.youtube.export

import nd.phuc.youtube.YouTube

suspend fun playlist(
    playlistId: String,
) {
    val result = YouTube.playlist(
        playlistId = playlistId,
    ).getOrNull() ?: return

    println("Playlist: $result")
}

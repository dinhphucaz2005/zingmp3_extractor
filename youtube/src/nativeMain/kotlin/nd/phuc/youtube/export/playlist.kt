package nd.phuc.youtube.export

import nd.phuc.youtube.YouTube

suspend fun playlist(
    playlistId: String,
) {
    val result = YouTube.playlist(
        playlistId = playlistId,
    ).getOrNull() ?: throw Exception("Failed to get playlist info")
    result.songs.forEach {
        println(
            createMetadata(
                ytItem = it,
                type = null,
                prefix = "playlist/${playlistId.sanitizeFileName()}",
            )
        )
    }
}

package nd.phuc.youtube.export

enum class Type(
    val dir: String,
) {
    THUMBNAILS("thumbnails"),
    VIDEO("videos"),
    PLAYLIST("playlists"),
    ALBUM("albums"),
    ARTIST("artists"),
}

package nd.phuc.youtube.utils

import nd.phuc.youtube.YouTube
import nd.phuc.youtube.pages.PlaylistPage
import io.ktor.utils.io.core.toByteArray
import java.security.MessageDigest

suspend fun Result<PlaylistPage>.completed() = runCatching {
    val page = getOrThrow()
    val songs = page.songs.toMutableList()
    var continuation = page.songsContinuation
    while (continuation != null) {
        val continuationPage = YouTube.playlistContinuation(continuation).getOrNull() ?: break
        songs += continuationPage.songs
        continuation = continuationPage.continuation
    }
    PlaylistPage(
        playlist = page.playlist,
        songs = songs,
        songsContinuation = null,
        continuation = page.continuation
    )
}

fun ByteArray.toHex(): String = joinToString("") { it.toHex() }

private fun Byte.toHex(): String {
    val hexChars = "0123456789abcdef"
    val b = this.toInt() and 0xFF
    return "${hexChars[b shr 4]}${hexChars[b and 0x0F]}"
}

fun sha1(str: String): String = MessageDigest.getInstance("SHA-1").digest(str.toByteArray()).toHex()

fun parseCookieString(cookie: String): Map<String, String> =
    cookie.split("; ")
        .filter { it.isNotEmpty() }
        .associate {
            val (key, value) = it.split("=")
            key to value
        }

fun String.parseTime(): Int? {
    try {
        val parts = split(":").map { it.toInt() }
        if (parts.size == 2) {
            return parts[0] * 60 + parts[1]
        }
        if (parts.size == 3) {
            return parts[0] * 3600 + parts[1] * 60 + parts[2]
        }
    } catch (e: Exception) {
        return null
    }
    return null
}

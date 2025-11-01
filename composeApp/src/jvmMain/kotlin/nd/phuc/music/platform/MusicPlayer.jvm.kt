package nd.phuc.music.platform

import nd.phuc.music.domain.model.Song
import nd.phuc.music.core.JvmApp

private var isInitialized = false
private var mpvProcess: Process? = null
private var currentSong: Song? = null
private var isPlaying: Boolean = false

actual fun initializeMusicPlayer() {
    isInitialized = true
    JvmApp.addOnAppDestroyListener {
        mpvProcess?.destroy()
    }
}

actual fun playSong(song: Song) {
    if (!isInitialized) {
        throw IllegalStateException("Music player is not initialized")
    }
    stopSong(song)

    val fileSource = when (song.source) {
        is Song.Source.LocalFile -> song.source
        is Song.Source.RemoteUrl -> throw IllegalArgumentException("Cannot play remote URL directly")
        is Song.Source.UriFile -> throw IllegalArgumentException("Cannot play URI file directly")
    }

    val filePath = fileSource.path
    mpvProcess = ProcessBuilder(
        "mpv",
        "--no-terminal",
        "--quiet",
        "--no-video",
        filePath
    )
        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
        .redirectError(ProcessBuilder.Redirect.DISCARD)
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .start()

    currentSong = song
    isPlaying = true
}


actual fun pauseSong(song: Song) {
    mpvProcess?.let {
        if (isPlaying) {
            it.destroy()
            isPlaying = false
        } else {
            playSong(song)
        }
    }
}

actual fun stopSong(song: Song) {
    mpvProcess?.destroy()
    mpvProcess = null
    isPlaying = false
}


package nd.phuc.music.platform

import nd.phuc.music.domain.model.Song

expect fun initializeMusicPlayer()
expect fun playSong(song: Song)

expect fun pauseSong(song: Song)

expect fun stopSong(song: Song)

object MusicPlayer {
    fun play(song: Song) {
        playSong(song)
    }

    fun pause(song: Song) {
        pauseSong(song)
    }

    fun stop(song: Song) {
        stopSong(song)
    }

    fun initialize() {
        initializeMusicPlayer()
    }

}
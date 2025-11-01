package nd.phuc.music.presentation.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nd.phuc.music.domain.model.Song
import nd.phuc.music.platform.MusicPlayer
import nd.phuc.music.platform.getLocalSongs
import nd.phuc.music.presentation.ui.component.FileImage


@Composable
fun MusicScreen(
    modifier: Modifier = Modifier,
    songs: List<Song> = getLocalSongs(),
) {
    var currentPlaySongId by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    val currentSong by remember {
        derivedStateOf {
            songs.find { it.id == currentPlaySongId } ?: songs.firstOrNull()
        }
    }


    LaunchedEffect(Unit) {
        MusicPlayer.initialize()
    }

    fun handlePlayPause(song: Song) {
        if (currentPlaySongId == song.id) {
            isPlaying = !isPlaying
        } else {
            currentPlaySongId = song.id
            isPlaying = true
        }
        // Assuming MusicPlayer.play(song) also handles pause/resume if the same song is passed
        MusicPlayer.play(song)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Use Crossfade to smoothly transition the background thumbnail when the song changes
        Crossfade(
            targetState = currentSong,
            animationSpec = tween(durationMillis = 400)
        ) { song ->
            if (song != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SongThumbnail(
                        song = song, modifier = Modifier.fillMaxSize()
                            .graphicsLayer {
                                renderEffect = BlurEffect(
                                    radiusX = 50f,
                                    radiusY = 50f,
                                    edgeTreatment = TileMode.Mirror
                                )
                            }
                    )
                }
            } else {
                Spacer(modifier = Modifier.fillMaxSize())
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            // Left Panel: Thumbnail and Controls
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.4f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                currentSong?.let { song ->
                    SongThumbnail(
                        song = song,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    NowPlayingSection(
                        song = song,
                        isPlaying = isPlaying,
                        onPrevious = {
                            val currentIndex = songs.indexOfFirst { it.id == song.id }
                            val previousIndex =
                                if (currentIndex - 1 < 0) songs.size - 1 else currentIndex - 1
                            val previousSong = songs[previousIndex]
                            handlePlayPause(previousSong)
                        },
                        onPlayPause = { handlePlayPause(song) },
                        onNext = {
                            val currentIndex = songs.indexOfFirst { it.id == song.id }
                            val nextIndex = (currentIndex + 1) % songs.size
                            val nextSong = songs[nextIndex]
                            handlePlayPause(nextSong)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Right Panel: Song List
            LazyColumn(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(songs, key = { it.id }) { song ->
                    DesktopSongItem(
                        song = song,
                        isCurrentSong = currentPlaySongId == song.id,
                        isPlaying = isPlaying && currentPlaySongId == song.id,
                        onPlayClick = { handlePlayPause(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SongThumbnail(
    song: Song,
    modifier: Modifier = Modifier,
) {
    FileImage(
        modifier = modifier,
        reloadKey = song.id
    ) {
        song.getFileThumbnail()
    }
}

@Composable
private fun DesktopSongItem(
    song: Song,
    isCurrentSong: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlayClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentSong) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        val contentColor =
            if (isCurrentSong) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        val subContentColor =
            if (isCurrentSong) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.7f
            )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, // Make title bold always for better readability
                    color = contentColor,
                    maxLines = 1
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subContentColor,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = song.duration,
                style = MaterialTheme.typography.bodySmall,
                color = subContentColor
            )
            if (isCurrentSong) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isPlaying) "⏸️" else "▶️",
                    style = MaterialTheme.typography.headlineSmall,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun NowPlayingSection(
    song: Song,
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            song.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            "by ${song.artist}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { 0.3f }, // placeholder
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Text("⏪", style = MaterialTheme.typography.headlineSmall)
            }
            IconButton(onClick = onPlayPause) {
                Text(
                    text = if (isPlaying) "⏸️" else "▶️",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            IconButton(onClick = onNext) {
                Text("⏩", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

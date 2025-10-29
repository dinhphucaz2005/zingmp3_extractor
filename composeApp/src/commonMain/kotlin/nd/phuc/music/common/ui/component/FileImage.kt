package nd.phuc.music.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter.Companion.DefaultTransform
import coil3.compose.AsyncImagePainter.State
import nd.phuc.music.platform.Timber
import java.io.File

// Simple in-memory cache to keep the last loaded File for a given key.
private object FileImageCache {
    val lastFileForKey = mutableMapOf<Any?, File>()
}

@Composable
fun FileImage(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    transform: (State) -> State = DefaultTransform,
    onState: ((State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
    // A key to indicate when to reload; callers should pass song.id (or a stable key).
    reloadKey: Any? = Unit,
    getFile: suspend () -> File
) {
    // Keep last successful file in state; we intentionally don't clear it when a new load starts,
    // so the previous image can be displayed until the new one is ready.
    val thumbnailState =
        remember { mutableStateOf(FileImageCache.lastFileForKey[reloadKey]) }

    LaunchedEffect(reloadKey) {
        try {
            val f = getFile()
            thumbnailState.value = f
            FileImageCache.lastFileForKey[reloadKey] = f
        } catch (t: Throwable) {
            Timber.d("Failed to load file image with message: ${t.message}")
        }
    }

    val file = thumbnailState.value

    if (file != null) {
        AsyncImage(
            modifier = modifier,
            model = file,
            contentDescription = contentDescription,
            transform = transform,
            onState = onState,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            clipToBounds = clipToBounds,
        )
    } else {
        Box(
            modifier = modifier.background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

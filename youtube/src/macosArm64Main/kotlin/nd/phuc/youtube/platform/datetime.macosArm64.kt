package nd.phuc.youtube.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long = memScoped {
    val tv = alloc<timeval>()
    gettimeofday(tv.ptr, null)
    return tv.tv_sec * 1000L + tv.tv_usec / 1000L
}

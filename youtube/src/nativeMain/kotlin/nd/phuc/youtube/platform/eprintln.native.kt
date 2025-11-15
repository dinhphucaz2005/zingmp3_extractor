@file:[OptIn(ExperimentalForeignApi::class)]

package nd.phuc.youtube.platform

import kotlinx.cinterop.ExperimentalForeignApi

actual fun eprintln(tag: String?, message: String) {
    val STDERR = platform.posix.fdopen(2, "w")
    if (tag != null)
        platform.posix.fprintf(STDERR, "%s\n", "${"[$tag]"}$message")
    else
        platform.posix.fprintf(STDERR, "%s\n", message)
    platform.posix.fflush(STDERR)
}
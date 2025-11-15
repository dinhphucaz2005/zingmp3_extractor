package nd.phuc.youtube.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun getHomeDirectory(): String {
    val home = getenv("HOME")?.toKString()
    return home ?: ""
}

actual fun mkdir(path: String): Boolean {
    val command = "mkdir -p \"$path\""
    val result = platform.posix.system(command)
    return result == 0
}
package nd.phuc.youtube.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.getenv
import kotlinx.cinterop.toKString

@OptIn(ExperimentalForeignApi::class)
actual fun getHomeDirectory(): String {
    getenv("USERPROFILE")?.toKString()?.let { return it }
    val drive = getenv("HOMEDRIVE")?.toKString()
    val path = getenv("HOMEPATH")?.toKString()
    if (drive != null && path != null) return drive + path
    return "C:\\"
}

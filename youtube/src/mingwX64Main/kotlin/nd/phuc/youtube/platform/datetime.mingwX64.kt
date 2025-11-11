package nd.phuc.youtube.platform

import kotlinx.cinterop.*
import platform.windows.*

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long {
    memScoped {
        val fileTime = alloc<FILETIME>()
        GetSystemTimeAsFileTime(fileTime.ptr)
        val time100ns = (fileTime.dwHighDateTime.toLong() shl 32) or
                (fileTime.dwLowDateTime.toLong() and 0xFFFFFFFF)
        val epochDiff = 11644473600000L
        return (time100ns / 10000L) - epochDiff
    }
}

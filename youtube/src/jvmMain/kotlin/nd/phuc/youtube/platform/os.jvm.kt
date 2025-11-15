package nd.phuc.youtube.platform

actual fun getHomeDirectory(): String {
    return System.getenv("HOME") ?: ""
}

actual fun mkdir(path: String): Boolean {
    val dir = java.io.File(path)
    return if (!dir.exists()) {
        dir.mkdirs()
    } else {
        dir.isDirectory
    }
}
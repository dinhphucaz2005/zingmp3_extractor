package nd.phuc.youtube.platform

actual fun getHomeDirectory(): String {
    return System.getenv("HOME") ?: ""
}
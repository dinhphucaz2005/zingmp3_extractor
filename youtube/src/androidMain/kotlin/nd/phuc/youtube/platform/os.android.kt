package nd.phuc.youtube.platform

actual fun getHomeDirectory(): String {
    throw NotImplementedError("getHomeDirectory is not implemented on this platform")
}
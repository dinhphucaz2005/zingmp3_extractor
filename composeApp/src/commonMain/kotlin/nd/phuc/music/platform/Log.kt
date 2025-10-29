package nd.phuc.music.platform

expect fun debugLog(message: String, tag: String? = null)

object Timber {

    fun d(message: String, tag: String? = null) {
        debugLog(message, tag)
    }
}
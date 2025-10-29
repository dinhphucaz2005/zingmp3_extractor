package nd.phuc.music.platform

actual fun debugLog(message: String, tag: String?) {
    print("${if (tag != null) "[$tag] " else ""}$message")
}
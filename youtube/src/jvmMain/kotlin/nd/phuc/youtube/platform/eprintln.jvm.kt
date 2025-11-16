package nd.phuc.youtube.platform

actual fun eprintln(tag: String?, message: String) {
    if (tag != null) {
        System.err.println("[$tag]$message")
    } else {
        System.err.println(message)
    }
}
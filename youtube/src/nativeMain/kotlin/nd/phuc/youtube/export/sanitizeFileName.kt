package nd.phuc.youtube.export

fun String?.sanitizeFileName(): String {
    if (this == null) return ""
    return replace(Regex("[\\\\/:*?\"<>|]"), "_")
}

package nd.phuc.music

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
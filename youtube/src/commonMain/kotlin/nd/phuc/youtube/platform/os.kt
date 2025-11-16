package nd.phuc.youtube.platform

expect fun getHomeDirectory(): String

expect fun mkdir(path: String): Boolean
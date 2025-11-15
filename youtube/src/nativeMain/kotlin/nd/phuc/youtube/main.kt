package nd.phuc.youtube

import kotlinx.coroutines.runBlocking
import nd.phuc.youtube.export.Config
import nd.phuc.youtube.export.home
import nd.phuc.youtube.export.playlist
import nd.phuc.youtube.export.search

const val HELP = """
Usage: <program> --search <query>
    --playlist <playlistId>   Export playlist with given playlistId
    --home                    Export home page
    --cookie <your_cookie>    Set cookie for authenticated requests
    --visitorId <visitor_id>  Set visitorId for requests
    --help                    Show this help message
"""

fun List<String>.getArgValue(flag: String): String? {
    val index = indexOf(flag)
    return if (index != -1 && index + 1 < size) this[index + 1] else null
}

fun main(args: Array<String>) = runBlocking {
    val arguments = args.toMutableList()
    addArgument(arguments)
    if (arguments.contains("--help") || arguments.isEmpty()) {
        println(HELP)
        return@runBlocking
    }


    arguments.getArgValue("--cookie")?.let { Config.cookie = it }
    arguments.getArgValue("--visitorId")?.let { Config.visitorId = it }

    arguments.getArgValue("--search")?.let { search(it) }

    arguments.getArgValue("--playlist")?.let { playlist(it) }

    if (arguments.contains("--home")) {
        home()
    }

    Config.httpClient.close();
}

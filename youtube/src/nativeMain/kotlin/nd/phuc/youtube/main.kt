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

fun Array<String>.getArgValue(flag: String): String? {
    val index = indexOf(flag)
    return if (index != -1 && index + 1 < size) this[index + 1] else null
}

fun main(args: Array<String>) = runBlocking {
    if (args.contains("--help") || args.isEmpty()) {
        println(HELP)
        return@runBlocking
    }


    args.getArgValue("--cookie")?.let { Config.cookie = it }
    args.getArgValue("--visitorId")?.let { Config.visitorId = it }

    when {
        args.contains("--search") -> args.getArgValue("--search")?.let {
            search(it)
        }

        args.contains("--playlist") -> args.getArgValue("--playlist")?.let {
            playlist(it)
        }

        args.contains("--home") -> home()
    }
}

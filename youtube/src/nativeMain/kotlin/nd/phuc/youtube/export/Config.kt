package nd.phuc.youtube.export

import nd.phuc.youtube.YouTube
import nd.phuc.youtube.platform.createClient
import nd.phuc.youtube.platform.getHomeDirectory
import nd.phuc.youtube.platform.mkdir


object Config {
    val appDirectory: String by lazy {
        getHomeDirectory() + "/Music/BeatShell"
    }
    val httpClient by lazy {
        createClient {
        }
    }

    init {
        mkdir(appDirectory)
    }

    var visitorId: String? = null
        set(value) {
            if (value != null) {
                field = value
                YouTube.visitorData = value
            }
        }
    var cookie: String? = null
        set(value) {
            field = value
            YouTube.cookie = value
        }

}

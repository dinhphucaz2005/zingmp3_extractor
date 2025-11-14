package nd.phuc.youtube.export

import nd.phuc.youtube.YouTube
import nd.phuc.youtube.platform.getHomeDirectory

object Config {
    val appDirectory: String by lazy {
        getHomeDirectory() + "/Music/BeatShell"
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

    fun load(tomlPath: String) {
        // No-op
    }
}

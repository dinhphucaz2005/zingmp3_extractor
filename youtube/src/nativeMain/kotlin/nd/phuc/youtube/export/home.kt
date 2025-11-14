package nd.phuc.youtube.export

import nd.phuc.youtube.YouTube
import okio.FileSystem
import okio.Path.Companion.toPath

suspend fun home() {

    val result = YouTube.home().getOrNull() ?: return
    val outputDir = "${Config.appDirectory}/home".toPath()
    FileSystem.SYSTEM.createDirectories(outputDir)

}
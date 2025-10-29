package nd.phuc.music

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import nd.phuc.music.core.JvmApp


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MusicNDP",
    ) {
        App()
    }
    Runtime.getRuntime().addShutdownHook(Thread {
        JvmApp.getOnAppDestroyListener().forEach { it.invoke() }
    })
}
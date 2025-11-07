package nd.phuc.mpv.client

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class JavaFXX11Pure : Application() {

    init {
        System.load("/home/phuc/StudioProjects/MusicNDP/libnativeembed.so")
    }

    external fun createNativeSurface(x: Int, y: Int, width: Int, height: Int): Long

    override fun start(primaryStage: Stage) {
        val pane = Pane()
        val scene = Scene(pane, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()

        Platform.runLater {
            // Lấy vị trí Pane trên màn hình (scene -> window)
            val bounds = pane.localToScreen(pane.boundsInLocal)
            createNativeSurface(
                bounds.minX.toInt(),
                bounds.minY.toInt(),
                bounds.width.toInt(),
                bounds.height.toInt()
            )
        }
    }
}

fun main() {
    Application.launch(JavaFXX11Pure::class.java)
}

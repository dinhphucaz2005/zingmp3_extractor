package nd.phuc.mpv.client

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MainApp : Application() {
    private lateinit var primaryStage: Stage
    private lateinit var nativeContainer: StackPane

    override fun start(stage: Stage) {
        primaryStage = stage
        println("=== Starting Application ===")

        val root = BorderPane()

        // Controls panel
        val controls = VBox(10.0).apply {
            children.addAll(
                Button("Start Native Render").apply {
                    setOnAction { startNativeRendering() }
                },
                Button("Debug Info").apply {
                    setOnAction { printDebugInfo() }
                }
            )
            padding = Insets(10.0)
        }

        // Native container với ID cụ thể
        nativeContainer = StackPane().apply {
            id = "native-container"
            style = "-fx-background-color: #000000; -fx-border-color: #00ff00; -fx-border-width: 3;"
            prefWidth = 640.0
            prefHeight = 480.0
        }

        root.left = controls
        root.center = nativeContainer

        val scene = Scene(root, 1000.0, 600.0)
        primaryStage.scene = scene
        primaryStage.title = "Embedded Native Rendering"

        // Debug scene graph
        scene.addPostLayoutPulseListener {
            println("Scene layout completed")
        }

        primaryStage.show()

        // Sử dụng Platform.runLater để đảm bảo scene đã ready
        Platform.runLater {
            println("Platform.runLater - Scene should be ready now")
            startNativeRendering()
        }
    }

    private fun printDebugInfo() {
        println("=== DEBUG INFO ===")
        println("Native Container: $nativeContainer")
        println("Container in scene: ${nativeContainer.scene != null}")
        println("Container bounds: ${nativeContainer.boundsInLocal}")
        println("Container layout bounds: ${nativeContainer.layoutBounds}")

        if (nativeContainer.scene != null) {
            val sceneBounds = nativeContainer.localToScene(nativeContainer.boundsInLocal)
            println("Container scene bounds: $sceneBounds")
            println("Container scene position: ${nativeContainer.localToScene(0.0, 0.0)}")
        }

        // Test JNI
        try {
            val windowId = getJavaFXWindowId()
            println("JavaFX Window ID: $windowId")
        } catch (e: Exception) {
            println("JNI test failed: ${e.message}")
        }
    }

    private fun startNativeRendering() {
        println("=== Starting Native Rendering ===")

        try {
            // Kiểm tra container
            if (!this::nativeContainer.isInitialized) {
                println("❌ Native container not initialized")
                return
            }

            if (nativeContainer.scene == null) {
                println("❌ Native container not in scene graph")
                return
            }

            // Lấy window ID
            val windowId = getJavaFXWindowId()
            println("✅ JavaFX Window ID: $windowId")

            // Tính toán vị trí và kích thước
            val sceneBounds = nativeContainer.localToScene(nativeContainer.boundsInLocal)
            val windowBounds = nativeContainer.localToScene(nativeContainer.layoutBounds)

            println("Scene bounds: $sceneBounds")
            println("Layout bounds: $windowBounds")

            // Sử dụng layout bounds để có kích thước thực tế
            val x = sceneBounds.minX.toInt()
            val y = sceneBounds.minY.toInt()
            val width = if (windowBounds.width > 10) windowBounds.width.toInt() else 640
            val height = if (windowBounds.height > 10) windowBounds.height.toInt() else 480

            println("Embedding at: ($x, $y) size: ${width}x$height")

            // Gọi native function
            createAndEmbedNativeWindow(windowId, x, y, width, height)
            println("✅ Native window creation called successfully")

        } catch (e: Exception) {
            println("❌ Error in startNativeRendering: ${e.message}")
            e.printStackTrace()
        }
    }

    private external fun getJavaFXWindowId(): Long
    private external fun createAndEmbedNativeWindow(
        parentWindowId: Long,
        x: Int, y: Int,
        width: Int, height: Int
    )

    companion object {
        init {
            try {
                System.load("/usr/local/bin/libnativeembed.so")
                println("✅ Native library loaded successfully!")
            } catch (e: UnsatisfiedLinkError) {
                println("❌ Failed to load native library: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                println("❌ Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
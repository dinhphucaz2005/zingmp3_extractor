plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    linuxX64("native") {
        binaries {
            executable {
                entryPoint = "nd.phuc.tui.main"
            }
        }
    }
    sourceSets["nativeMain"].languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
}
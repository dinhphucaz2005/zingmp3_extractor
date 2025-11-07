import org.gradle.api.tasks.JavaExec

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.javafx)
    id("application")
}

javafx {
    version = "22"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}


application {
    mainClass.set("nd.phuc.mpv.client.MainKt")
}

// Ensure the `run` task starts the JVM with LC_NUMERIC=C in the environment so native libs
// that check the C locale (like libmpv) won't crash on non-C locales. This helps when
// class initialization order causes native libraries to be loaded before any explicit
// setlocale call in Kotlin/Java code.
tasks.named<JavaExec>("run") {
    environment("LC_NUMERIC", "C")
}

dependencies {
    implementation(libs.jna.platform)
}

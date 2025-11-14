@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    androidLibrary {
        namespace = "nd.phuc.youtube"
        compileSdk = 36
        minSdk = 24
    }

    jvm()

    linuxX64 {
        binaries {
            executable {
                entryPoint = "nd.phuc.youtube.main"
            }
            sharedLib {
                baseName = "youtube"
            }
        }
    }

    mingwX64 {
        binaries {
            executable {
                entryPoint = "nd.phuc.youtube.main"
            }
            sharedLib {
                baseName = "youtube"
            }
        }
    }

    macosArm64 {
        binaries {
            executable {
                entryPoint = "nd.phuc.youtube.main"
            }
            sharedLib {
                baseName = "youtube"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.bundles.ktor.client)
                implementation(libs.krypto)
                implementation(libs.kotlinx.io.core)
                implementation(libs.okio)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }

        linuxX64Main {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }

        mingwX64Main {
            dependencies {
                implementation(libs.ktor.client.windows)
            }
        }
        macosArm64Main {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }

}

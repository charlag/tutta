import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable

plugins {
    kotlin("multiplatform")
}


kotlin {
    jvm()
    linuxX64()

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.12")
            }
        }
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }

}
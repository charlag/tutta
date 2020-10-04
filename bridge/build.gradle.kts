plugins {
    kotlin("multiplatform")
}

kotlin {
    linuxX64("linux") {
        binaries {
            executable {
                linkerOpts("-L/usr/lib64")
            }
        }
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
        }
        val linuxMain by getting {
            dependencies {
                implementation(project(":SharedCode"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")
            }
        }
        val linuxTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }

}
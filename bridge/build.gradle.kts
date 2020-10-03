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
        val linuxMain by getting {
            dependencies {
                implementation(project(":SharedCode"))
            }
        }
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }

}
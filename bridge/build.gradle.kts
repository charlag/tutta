import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings

plugins {
    kotlin("multiplatform")
}

kotlin {
    linuxX64("linux") {
        binaries {
            executable {
                linkerOpts("-L/usr/lib64")
                linkerOpts.addAll(runPkgConfig("libsecret-1", cflags = false, libs = true))
            }
        }
        compilations["main"].cinterops {
            val sqlite by creating {
                packageName("org.sqlite")
            }
            val libsecret by creating {
                packageName("org.libsecret")
                pkgConfig("libsecret-1", cflags = true, libs = false)
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

/**
 * Runs `pkg-config`:
 * https://github.com/JetBrains/kotlin-native/issues/1534#issuecomment-384894431
 *
 * taken from https://gist.github.com/micolous/c00b14b2dc321fdb0eab8ad796d71b80
 */
fun runPkgConfig(
    vararg packageNames: String,
    cflags: Boolean = false,
    libs: Boolean = false): List<String> {
    val p = ProcessBuilder(*(listOfNotNull(
        "pkg-config",
        if (cflags) "--cflags-only-I" else null,
        if (libs) "--libs" else null
    ).toTypedArray() + packageNames)).run {
        // https://github.com/JetBrains/kotlin-native/issues/3484#issuecomment-544926683
        environment()["PKG_CONFIG_ALLOW_SYSTEM_LIBS"] = "1"
        start()
    }.also { it.waitFor(10, TimeUnit.SECONDS) }

    if (p.exitValue() != 0) {
        throw GradleException("Error executing pkg-config: ${p.errorStream.bufferedReader().readText()}")
    }

    return p.inputStream.bufferedReader().readText().split(" ").map{ it.trim() }
}

fun DefaultCInteropSettings.pkgConfig(
    vararg packageNames: String,
    cflags: Boolean = true,
    libs: Boolean = true) {
    if (cflags) {
        compilerOpts.addAll(runPkgConfig(*packageNames, cflags=true).also {
            println("cflags: $it")
        })
    }

    if (libs) {
        linkerOpts.addAll(runPkgConfig(*packageNames, libs=true).also {
            println("libs: $it")
        })
    }
}
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

val LIBSECRET_PKG = "libsecret-1"
val SQLITE_PKG = "sqlite3"

kotlin {
    linuxX64("linux") {
        binaries {
            executable {
                linkerOpts("-L/usr/lib64")
                pkgConfig(LIBSECRET_PKG, SQLITE_PKG)
            }
        }
        compilations["main"].cinterops {
            val sqlite by creating {
                packageName("org.sqlite")
                pkgConfig(SQLITE_PKG)
            }
            val libsecret by creating {
                packageName("org.libsecret")
                pkgConfig(LIBSECRET_PKG)
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
                implementation(project(":mailutil"))
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
    libs: Boolean = false
): List<String> {
    val p = ProcessBuilder(
        *(listOfNotNull(
            "pkg-config",
            if (cflags) "--cflags-only-I" else null,
            if (libs) "--libs" else null
        ).toTypedArray() + packageNames)
    ).run {
        // https://github.com/JetBrains/kotlin-native/issues/3484#issuecomment-544926683
        environment()["PKG_CONFIG_ALLOW_SYSTEM_LIBS"] = "1"
        start()
    }.also { it.waitFor(10, TimeUnit.SECONDS) }

    if (p.exitValue() != 0) {
        throw GradleException(
            "Error executing pkg-config: ${
                p.errorStream.bufferedReader().readText()
            }"
        )
    }

    return p.inputStream.bufferedReader().readText().split(" ").map { it.trim() }
}

/**
 * Add include ("-Isomething") flags to cinterop so it can find headers.
 */
fun DefaultCInteropSettings.pkgConfig(vararg packageNames: String) {
    compilerOpts.addAll(runPkgConfig(*packageNames, cflags = true))
}

/**
 * Add include ("-Isomething") flags to cinterop so it can find headers.
 */
fun Executable.pkgConfig(vararg packageNames: String) {
    linkerOpts.addAll(runPkgConfig(*packageNames, libs = true))
}
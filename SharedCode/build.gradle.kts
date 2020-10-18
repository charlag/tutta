plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(rootProject.extra["android_compileSdkVersion"] as Int)
    defaultConfig {
        minSdkVersion(rootProject.extra["android_minSdkVersion"] as Int)
        targetSdkVersion(rootProject.extra["android_targetSdkVersion"] as Int)
    }
}

kotlin {
    android()
    linuxX64 {
        compilations["main"].cinterops {
            val openssl by creating {
                packageName("org.openssl")
                pkgConfig("openssl")
            }
        }
    }
//    js()

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.InternalSerializationApi")
            languageSettings.useExperimentalAnnotation("kotlinx.serialization.ExperimentalSerializationApi")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
        }

        val ktor_version: String by rootProject.extra
        val serialization_version: String by rootProject.extra

        val commonMain by getting {
            kotlin.srcDir("$buildDir/generated/source/kotlin")
            dependencies {
                implementation(kotlin("stdlib-common"))
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serialization_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-json:$ktor_version")
                api("io.ktor:ktor-client-logging:$ktor_version")
                implementation("io.ktor:ktor-client-websockets:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                api("io.ktor:ktor-client-core:$ktor_version")
                api("io.ktor:ktor-client-okhttp:$ktor_version")
                implementation("io.ktor:ktor-client-serialization-jvm:$ktor_version")
                implementation("at.favre.lib:bcrypt:0.8.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
                api("io.ktor:ktor-client-logging-jvm:$ktor_version")
                api("io.ktor:ktor-client-logging:$ktor_version")
                implementation("org.lz4:lz4-java:1.7.1")
                api("io.ktor:ktor-client-websockets:$ktor_version")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.12")
            }
        }
        val linuxX64Main by getting {
            dependencies {
                api("io.ktor:ktor-client-core-linuxx64:$ktor_version")
                api("io.ktor:ktor-client-curl:$ktor_version")
                api("io.ktor:ktor-client-json-linuxx64:$ktor_version")
                api("io.ktor:ktor-client-logging-linuxx64:$ktor_version")
                api("io.ktor:ktor-client-serialization-linuxx64:$ktor_version")
                // See notes where httpClient is created on why it's not possible to use websockets
//                implementation("io.ktor:ktor-client-websockets:$ktor_version")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt-2")
            }
        }
    }
}

tasks.register("copyJsFiles", Copy::class) {
    from("sc/jsMail/js")
    into("${buildDir}/classes/kotlin/js/main")
}


val generateModels = tasks.register<GenerateModels>("generateModelClasses") {
    modelsFile = File("$projectDir/metadata/models.json")
    outputDir = File("$buildDir/generated/source/kotlin")

    doLast {
        generate()
    }
}

// This DSL class KotlinCompile is the only common thing between different Kotlin compile tasks
// There is probably a better way to do this
tasks.matching { it is org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*> }.forEach {
    it.dependsOn(generateModels)
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
fun org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings.pkgConfig(vararg packageNames: String) {
    compilerOpts.addAll(runPkgConfig(*packageNames, cflags = true))
}
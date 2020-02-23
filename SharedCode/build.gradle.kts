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
    compileOptions {
        setSourceCompatibility("1.8")
        setTargetCompatibility("1.8")
    }
}

kotlin {
    android()
    js()
// This was used in the build.gradle before, it's not clear if we'll need it now
//    js {
//        compileKotlinJs {
//            kotlinOptions {
//                metaInfo = true
//                sourceMap = true
//                sourceMapEmbedSources = "always"
//                moduleKind = "umd"
//                target = "v5"
//            }
//        }
//    }

    sourceSets {
        val ktor_version: String by rootProject.extra
        val serialization_version: String by rootProject.extra

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serialization_version")
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-json:$ktor_version")
                api("io.ktor:ktor-client-logging:$ktor_version")
                implementation("io.ktor:ktor-client-websockets:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")

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
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.61")
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
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                api("io.ktor:ktor-client-core:$ktor_version")
                api("io.ktor:ktor-client-js:$ktor_version")
                api("io.ktor:ktor-client-core-js:$ktor_version")
                api("io.ktor:ktor-client-json-js:$ktor_version")
                implementation("io.ktor:ktor-client-serialization-js:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serialization_version")
                api("io.ktor:ktor-client-logging-js:$ktor_version")

            }
        }
        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
            }
        }
    }
}

tasks.register("copyJsFiles", Copy::class) {
    from("sc/jsMail/js")
    into("${buildDir}/classes/kotlin/js/main")
}

//task copyJsFiles(type: Copy) {
//    from "src/jsMain/js"
//
//    into "${buildDir}/classes/kotlin/js/main"
//
//    jsMainClasses.dependsOn copyJsFiles
//}

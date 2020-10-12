// Top-level build file where you can add configuration options common to all sub-projects/modules.

val kotlin_version = "1.4.0"

buildscript {
    val kotlin_version by rootProject.extra("1.4.10")
    rootProject.apply {
        extra["ktor_version"] = "1.4.1"
        extra["coroutines_version"] = "1.3.9"
        extra["serialization_version"] = "1.0.0-RC2"
        extra["android_compileSdkVersion"] = 29
        extra["android_minSdkVersion"] = 24
        extra["android_targetSdkVersion"] = 29
    }

    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        mavenCentral()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")

        classpath("com.moowork.gradle:gradle-node-plugin:1.2.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://dl.bintray.com/hotkeytlt/maven")
    }
}
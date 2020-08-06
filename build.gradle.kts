// Top-level build file where you can add configuration options common to all sub-projects/modules.

val kotlin_version = "1.3.70"

buildscript {
    val kotlin_version by rootProject.extra("1.3.70")
    rootProject.apply {
        extra["ktor_version"] = "1.3.0"
        extra["coroutines_version"] = "1.3.3"
        extra["serialization_version"] = "0.14.0"
        extra["android_compileSdkVersion"] = 28
        extra["android_minSdkVersion"] = 24
        extra["android_targetSdkVersion"] = 28
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
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")

        classpath("com.moowork.gradle:gradle-node-plugin:1.2.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

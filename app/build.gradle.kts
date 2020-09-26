plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(rootProject.extra["android_compileSdkVersion"] as Int)
    defaultConfig {
        applicationId = "com.charlag.tuta"
        minSdkVersion(rootProject.extra["android_minSdkVersion"] as Int)
        targetSdkVersion(rootProject.extra["android_targetSdkVersion"] as Int)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kapt {
            arguments {
                arg("room.incremental", "true")
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
    compileOptions {
        setSourceCompatibility("1.8")
        setTargetCompatibility("1.8")
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":SharedCode"))

    val lifecycle_version = "2.2.0"
    val room_version = "2.2.3"
    val dagger_version = "2.26"
    val kotlin_version: String  by rootProject.extra
    val coroutines_version: String by rootProject.extra
    val serialization_version: String by rootProject.extra

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.recyclerview:recyclerview-selection:1.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")

    implementation("androidx.biometric:biometric:1.0.1")

    implementation("androidx.fragment:fragment-ktx:1.2.5")
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")

    implementation("net.zetetic:android-database-sqlcipher:4.3.0@aar")

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    implementation("com.google.dagger:dagger-android-support:$dagger_version")
    kapt("com.google.dagger:dagger-compiler:$dagger_version")
    kapt("com.google.dagger:dagger-android-processor:$dagger_version")

    testImplementation("junit:junit:4.12")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
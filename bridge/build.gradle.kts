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
        compilations["main"].cinterops {
            val openssl by creating {
                packageName("org.openssl")
                includeDirs.headerFilterOnly("/usr/include")
            }
        }
    }

    sourceSets {
        val linuxMain by getting {
            dependencies {
                implementation(project(":SharedCode"))
            }
        }
    }

}
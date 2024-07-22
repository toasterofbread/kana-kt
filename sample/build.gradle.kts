plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    val native_targets = listOf(
        linuxX64(),
        linuxArm64(),
        mingwX64()
    )

    for (target in native_targets) {
        target.binaries {
            executable {
                entryPoint = "dev.toastbits.sample.main"

                if (target.name == "mingwX64") {
                    linkerOpts("-l:libstdc++.a")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":library"))
            }
        }
    }
}

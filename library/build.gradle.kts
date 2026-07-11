import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.livingwithhippos"
version = "0.1.2"

kotlin {
    jvm()
    androidLibrary {
        namespace = "io.github.livingwithhippos.torbox"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(
                        JvmTarget.JVM_11,
                    )
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }
        // browser {
        //     testTask {
        //         useKarma {
        //             useFirefox()
        //         }
        //     }
        // }
        useEsModules()
        binaries.library()
        // generateTypeScriptDefinitions()
        compilerOptions {
            optIn.add("kotlin.js.ExperimentalJsExport")
        }
    }

    sourceSets {
        commonMain.dependencies {
            // put your multiplatform dependencies here
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.coroutines.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)

            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlin.coroutines.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        linuxX64Main.dependencies {
            implementation(libs.ktor.client.curl)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.kotlinx.browser)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "torbox-kmp-client", version.toString())

    pom {
        name = "Torbox KMP Client"
        description = "An unofficial KMP client library for Torbox"
        inceptionYear = "2026"
        url = "https://github.com/LivingWithHippos/torbox-kmp-client"
        licenses {
            license {
                name = "GNU General Public License v3.0"
                url = "https://spdx.org/licenses/GPL-3.0-or-later.html"
                distribution = "https://spdx.org/licenses/GPL-3.0-or-later.html"
            }
        }
        developers {
            developer {
                id = "livingwithhippos"
                name = "LivingWithHippos"
                email = "livingwithhippos@gmail.com"
                url = "https://github.com/LivingWithHippos"
                organization = "LivingWithHippos"
                organizationUrl = "https://github.com/LivingWithHippos"
            }
        }
        scm {
            url = "https://github.com/LivingWithHippos/torbox-kmp-client/tree/master"
            connection = "scm:git:git://github.com/LivingWithHippos/torbox-kmp-client.git"
            developerConnection = "scm:git:ssh://git@github.com/LivingWithHippos/torbox-kmp-client.git"
        }
    }
}

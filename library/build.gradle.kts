import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.github.livingwithhippos"
version = "1.0.0"

kotlin {
    jvm()
    androidLibrary {
        namespace = "com.github.livingwithhippos.torbox.kmp.client"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(
                    JvmTarget.JVM_11
                )
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
    js {
        browser {
        testTask {
            useKarma {
                useFirefox()
                useChrome()
                useSafari()
            }
        }
    }
        useEsModules()
        binaries.executable()

        compilerOptions {
            optIn.add("kotlin.js.ExperimentalJsExport")
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
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

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "Torbox KMP Client"
        description = "A client library for Torbox"
        inceptionYear = "2026"
        url = "https://github.com/LivingWithHippos/torbox-kmp-client"
        licenses {
            license {
                name = "XXX"
                url = "YYY"
                distribution = "ZZZ"
            }
        }
        developers {
            developer {
                id = "XXX"
                name = "LivingWithHippos"
                url = "ZZZ"
            }
        }
        scm {
            url = "XXX"
            connection = "YYY"
            developerConnection = "ZZZ"
        }
    }
}

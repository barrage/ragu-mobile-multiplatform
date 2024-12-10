import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

/**
 * Gradle build configuration for the ChatWhitelabel Kotlin Multiplatform project.
 * This file sets up the project for Android and iOS targets, configures dependencies,
 * and defines build settings.
 */

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    /**
     * Android target configuration
     */
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

    /**
     * iOS targets configuration
     * Configures framework for iOS targets (x64, arm64, simulatorArm64)
     */
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.rinku)
        }
    }

    // jvm("desktop")

    sourceSets {
        // val desktopMain by getting

        /**
         * Android-specific dependencies
         */
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform(libs.firebase.android.bom))
            implementation(libs.firebase.android.crashlytics.ktx)
            implementation(libs.ktor.client.cio)
            api(libs.gitlive.firebase.kotlin.crashlytics)
            implementation(libs.androidx.startup)
        }

        /**
         * iOS-specific dependencies
         */
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            api(libs.gitlive.firebase.kotlin.crashlytics)
        }

        /**
         * Common dependencies for all platforms
         */
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.landscapist.coil)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.compottie)
            implementation(libs.konnection)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.napier)
            implementation(libs.material.kolor)
            api(libs.rinku)
            implementation(libs.rinku.compose.ext)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(project.dependencies.platform(libs.kotlin.crypto.hash.bom))
            implementation(libs.kotlin.crypto.hash.sha2)
            implementation(libs.kotlin.crypto.secure.random)
            implementation(libs.multiplatform.markdown.renderer.m3)
            implementation(libs.multiplatform.markdown.renderer.code)
        }

        /*desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
        }*/
        getByName("commonMain") { dependencies { implementation(libs.kotlinx.coroutines.core) } }
    }
}

/**
 * Android-specific configuration
 */
android {
    namespace = "net.barrage.chatwhitelabel"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.barrage.chatwhitelabel"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 4
        versionName = "0.0.4"
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

    /**
     * Signing configurations for release builds
     */
    signingConfigs {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        if (keystorePropertiesFile.exists()) {
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            create("release") {
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
            }
        } else {
            create("release") {}
        }
    }

    /**
     * Build types configuration
     */
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { buildConfig = true }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

/*compose.desktop {
     application {
         mainClass = "net.barrage.chatwhitelabel.MainKt"

         nativeDistributions {
             targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
             packageName = "net.barrage.chatwhitelabel"
             packageVersion = "0.0.1"
         }
     }
 }*/
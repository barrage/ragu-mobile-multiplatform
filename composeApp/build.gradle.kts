import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

/**
 * Gradle build configuration for the Ragu Kotlin Multiplatform project.
 * This file sets up the project for Android and iOS targets, configures dependencies,
 * and defines build settings.
 */

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.gradle.buildconfig.plugin)
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
            implementation(libs.firebase.android.common.ktx)
            implementation(libs.ktor.client.cio)
            implementation(libs.androidx.startup)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.accompanist.permissions)
            implementation(libs.camera.camera2)
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
            implementation(libs.kotlinx.coroutines.guava)
        }

        /**
         * iOS-specific dependencies
         */
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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
            implementation(libs.reveal.core)
            implementation(libs.reveal.shapes)
            api(libs.gitlive.firebase.kotlin.crashlytics)
            implementation(libs.peekaboo.image.picker)
            api(libs.moko.permissions)
            implementation(libs.moko.camera)
            implementation(libs.krop.core)
            implementation(libs.essenty.lifecycle)
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
    namespace = "net.barrage.ragu"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.barrage.ragu"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "0.2.0"
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
buildConfig {
    val configFile = File(rootDir, "config.properties")
    if (!configFile.exists()) {
        throw GradleException(
            """
            Configuration file not found!
            Please copy 'config.example.properties' to 'config.properties' and update the values.
            
            cp config.example.properties config.properties
        """.trimIndent()
        )
    }

    val prop = Properties().apply {
        load(FileInputStream(configFile))
    }

    // List of required properties
    val requiredProperties = listOf(
        "BASE_URL",
        "GOOGLE_AUTH_URL",
        "AAI_AUTH_URL",
        "GOOGLE_CLIENT_ID",
        "AAI_CLIENT_ID",
        "REDIRECT_HOST",
        "REDIRECT_PATH"
    )

    // Check for missing properties
    val missingProperties = requiredProperties.filter { prop.getProperty(it).isNullOrBlank() }
    if (missingProperties.isNotEmpty()) {
        throw GradleException(
            """
Missing required properties in config.properties:
${missingProperties.joinToString("\n") { "- $it" }}       
Please check config.example.properties for the required format.
        """.trimIndent()
        )
    }

    // Build config fields
    buildConfigField("String", "BASE_URL", "\"${prop.getProperty("BASE_URL")}\"")
    buildConfigField("String", "GOOGLE_AUTH_URL", "\"${prop.getProperty("GOOGLE_AUTH_URL")}\"")
    buildConfigField("String", "AAI_AUTH_URL", "\"${prop.getProperty("AAI_AUTH_URL")}\"")
    buildConfigField(
        "String",
        "GOOGLE_CLIENT_ID",
        "\"${prop.getProperty("GOOGLE_CLIENT_ID")}\""
    )
    buildConfigField("String", "AAI_CLIENT_ID", "\"${prop.getProperty("AAI_CLIENT_ID")}\"")
    buildConfigField("String", "REDIRECT_HOST", "\"${prop.getProperty("REDIRECT_HOST")}\"")
    buildConfigField("String", "REDIRECT_PATH", "\"${prop.getProperty("REDIRECT_PATH")}\"")
}

/*compose.desktop {
     application {
         mainClass = "net.barrage.ragu.MainKt"

         nativeDistributions {
             targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
             packageName = "net.barrage.ragu"
             packageVersion = "0.1.0"
         }
     }
 }*/
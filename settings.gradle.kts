rootProject.name = "ChatWhitelabel"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        val prop = java.util.Properties().apply {
            load(java.io.FileInputStream(File(rootDir, "local.properties")))
        }
        maven {
            credentials {
                username = prop["nexusUsername"].toString()
                password = prop["nexusPassword"].toString()
            }
            url = java.net.URI(prop["nexusUrl"].toString())
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
pluginManagement {
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        maven {
            url = uri("https://maven.google.com/")
        }
    }
}

rootProject.name = "kpbrovka"
include(":app")
 
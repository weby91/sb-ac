pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EdisonAndroidExercise"

include(":app")
include(":core")
include(":data")
include(":domain")
include(":ui")
include(":feature:fact")
include(":testutils")

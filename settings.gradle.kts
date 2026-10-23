pluginManagement {
    repositories {
        // Direct first — fastest and works on GitHub Actions
        google()
        mavenCentral()
        gradlePluginPortal()
        // Fallback for Iranian network
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        // Direct first — GitHub Actions has no filtering
        google()
        mavenCentral()
        // Fallback for local Iranian dev
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "NumDoz"
include(":composeApp")

pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.tapsell.ir")
        maven("https://repo.huaweicloud.com/repository/maven/")
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.tapsell.ir")
        maven("https://repo.huaweicloud.com/repository/maven/")
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/compose/dev")
    }
}

rootProject.name = "NumDoz"
include(":composeApp")

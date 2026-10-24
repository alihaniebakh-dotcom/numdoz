import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.application")
}

kotlin {
    jvmToolchain(22)
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }
    jvm("desktop") { compilerOptions { jvmTarget.set(JvmTarget.JVM_22) } }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.animation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.xerial:sqlite-jdbc:3.45.3.0")
                implementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("ir.tapsell:tapsell:1.3.0")
                implementation("ir.tapsell.mediation.adapter:legacy:1.3.0")
                implementation("androidx.activity:activity-compose:1.9.3")
            }
        }
    }
}

compose.resources {
    packageOfResClass = "ir.numdoz.generated.resources"
}

android {
    namespace = "ir.numdoz"
    compileSdk = 34
    defaultConfig {
        addManifestPlaceholders(
            mapOf(
                "TapsellMediationAppKey" to "mlrdeocclskleaatmimrmklbgtkprrggfafenjcolbdaolmetljbkmimjbfslnlehjaimk"
            )
        )
        applicationId = "ir.numdoz"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}

compose.desktop {
    application { mainClass = "ir.numdoz.MainKt" }
}


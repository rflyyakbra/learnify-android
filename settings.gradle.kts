// Konfigurasi Manajemen Plugin (Plugin Management)
pluginManagement {
    repositories {
        // Repositori Google untuk plugin Android
        google()
        // Repositori Maven Central
        mavenCentral()
        // Repositori Portal Plugin Gradle
        gradlePluginPortal()
    }
}

// Konfigurasi Manajemen Resolusi Dependensi (Dependency Resolution Management)
dependencyResolutionManagement {
    // Menggunakan operator penugasan '=' untuk RepositoriesMode (sintaks Kotlin DSL)
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        // Repositori Google
        google()
        // Repositori Maven Central
        mavenCentral()
        // Repositori JitPack, menggunakan url = uri("...") untuk sintaks yang benar
        maven { url = uri("https://jitpack.io") }
    }
}

// Nama Proyek Utama
rootProject.name = "learnify"

// Modul yang disertakan
include(":app")
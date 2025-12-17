plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.learnify"
    compileSdk = 36 // Pastikan Android Studio kamu support SDK ini (biasanya 34/35 yang stabil)

    defaultConfig {
        applicationId = "com.example.learnify"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // --- ANDROID CORE & UI (Bawaan Project) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- FIREBASE & GOOGLE SIGN IN (BAGIAN KRUSIAL) ---
    // 1. Platform BOM: Mengatur semua versi Firebase agar cocok satu sama lain
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // 2. Firebase Auth: Tidak perlu tulis versi lagi, ikut BOM
    implementation("com.google.firebase:firebase-auth")

    // 3. Google Play Services Auth: WAJIB ADA untuk Login Google (GoogleSignInClient)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- LIBRARY TAMBAHAN ---
    // Grafik
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
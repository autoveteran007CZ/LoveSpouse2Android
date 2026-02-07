plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.lovespouse2android"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.0.5") // Jetpack Compose UI
    implementation("androidx.compose.material:material:1.0.5") // Material Design
    implementation("androidx.activity:activity-compose:1.3.1") // Activity Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0") // Lifecycle
    implementation("androidx.navigation:navigation-compose:2.4.0") // Navigation Component
    implementation("com.polidea.rxandroidble2:rxandroidble:1.12.1") // Bluetooth BLE Support
}

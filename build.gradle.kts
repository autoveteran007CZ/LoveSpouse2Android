plugins { 
    id("com.android.application") version "7.2.1" apply false 
    id("kotlin-android") version "1.6.10" apply false 
}

android { 
    namespace "com.example.lovespouse"
    compileSdk = 31

    defaultConfig { 
        applicationId = "com.example.lovespouse"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0" 
    }
}

dependencies { 
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10") 
} 

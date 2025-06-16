import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.sumup"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sumup"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            val apiKey = localProperties.getProperty("GEMINI_API_KEY") ?: "your_gemini_api_key_here"
            buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
        }
        release {
            isMinifyEnabled = true
            val apiKey = localProperties.getProperty("GEMINI_API_KEY") ?: "your_gemini_api_key_here"
            buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
        )
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // PDFBox specific excludes
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.core:core-splashscreen:1.0.1")
    
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
    
    // Swipe actions for Compose
    implementation("me.saket.swipe:swipe:1.2.0")
    
    // Shimmer effect
    implementation(libs.compose.shimmer)
    
    // Window management for adaptive layouts
    implementation(libs.androidx.window.core)
    implementation(libs.androidx.material3.window.size)
    
    // Predictive back gesture support
    implementation(libs.androidx.activity)
    
    // ViewModel & Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    
    // Camera & ML Kit
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    
    // PDF Processing (PDFBox Android)
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")
    // TODO: Add PDF viewer later if needed
    // implementation("com.github.barteksc:android-pdf-viewer:2.8.2")
    
    // Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("io.mockk:mockk-android:1.13.12")
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

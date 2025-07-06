import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    // Tạm thời comment để build được nhiều flavors
    // id("com.google.gms.google-services")
    // id("com.google.firebase.crashlytics")
    // id("com.google.firebase.firebase-perf")
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
        versionCode = 3
        versionName = "1.0.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            // Không load API key từ local.properties - bắt user phải tự nhập
            buildConfigField("String", "GEMINI_API_KEY", "\"\"")
        }
        release {
            isMinifyEnabled = true
            // Không load API key từ local.properties - bắt user phải tự nhập
            buildConfigField("String", "GEMINI_API_KEY", "\"\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    // Tạo nhiều flavor để có thể cài nhiều phiên bản trên cùng thiết bị
    flavorDimensions += "version"
    productFlavors {
        create("dev") {
            dimension = "version"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "SumUp Dev")
            // Disable Firebase for dev flavor
            extra["enableCrashlytics"] = false
        }
        create("staging") {
            dimension = "version"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            resValue("string", "app_name", "SumUp Staging")
            // Disable Firebase for staging flavor
            extra["enableCrashlytics"] = false
        }
        create("prod") {
            dimension = "version"
            resValue("string", "app_name", "SumUp")
            // Enable Firebase only for prod
            extra["enableCrashlytics"] = true
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
            // Apache POI specific excludes
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "schemaorg_apache_xmlbeans/system/sD023D6490046BA0250A839A9AD24C443/**"
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
    implementation("androidx.compose.runtime:runtime-livedata:1.7.6")
    
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
    
    // Chucker - HTTP Inspector
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")
    
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
    
    // Document Processing
    implementation("com.tom-roush:pdfbox-android:2.0.27.0") // PDF
    implementation("org.zwobble.mammoth:mammoth:1.5.0") // DOCX
    
    // For now, we'll implement basic text extraction for other formats
    // Apache POI requires API 26+, which would break compatibility with API 24
    // Alternative: Using Mammoth for DOCX support which works with API 24+
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    // TODO: Add PDF viewer later if needed
    // implementation("com.github.barteksc:android-pdf-viewer:2.8.2")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    
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

package com.example.sumup

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.perf.performance
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SumUpApplication : Application() {
    
    companion object {
        lateinit var analytics: FirebaseAnalytics
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase services
        initializeFirebase()
        
        // Initialize PDFBox Android resources
        PDFBoxResourceLoader.init(applicationContext)
    }
    
    private fun initializeFirebase() {
        try {
            // Initialize Analytics
            analytics = Firebase.analytics
            
            // Configure Crashlytics
            Firebase.crashlytics.apply {
                setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
                setCustomKey("app_version", BuildConfig.VERSION_NAME)
                setCustomKey("build_type", BuildConfig.BUILD_TYPE)
            }
            
            // Configure Performance Monitoring
            Firebase.performance.apply {
                isPerformanceCollectionEnabled = !BuildConfig.DEBUG
            }
            
            // Log app start event
            analytics.logEvent("app_started", null)
            
        } catch (e: Exception) {
            // Firebase might not be configured yet (missing google-services.json)
            // App should still work without Firebase
            android.util.Log.w("SumUpApplication", "Firebase initialization failed: ${e.message}")
        }
    }
}
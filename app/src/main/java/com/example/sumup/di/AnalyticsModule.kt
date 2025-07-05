package com.example.sumup.di

import android.content.Context
import com.example.sumup.utils.analytics.AnalyticsHelper
import com.example.sumup.utils.analytics.FirebaseAnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return try {
            FirebaseAnalytics.getInstance(context)
        } catch (e: Exception) {
            // Return a no-op instance if Firebase is not configured
            android.util.Log.w("AnalyticsModule", "Firebase Analytics not available: ${e.message}")
            // For now, return the instance anyway - it will be no-op if not configured
            FirebaseAnalytics.getInstance(context)
        }
    }
    
    @Provides
    @Singleton
    fun provideAnalyticsHelper(
        firebaseAnalytics: FirebaseAnalytics
    ): AnalyticsHelper {
        return FirebaseAnalyticsHelper(firebaseAnalytics)
    }
}
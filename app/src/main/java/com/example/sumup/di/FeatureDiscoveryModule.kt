package com.example.sumup.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
// import com.example.sumup.domain.usecase.EnhancedFeatureDiscoveryUseCase
import com.example.sumup.domain.usecase.FeatureDiscoveryUseCase
// import com.example.sumup.utils.analytics.AnalyticsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FeatureDiscoveryDataStore

// DataStore for feature discovery
private val Context.featureDiscoveryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "feature_discovery_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object FeatureDiscoveryModule {
    
    @Provides
    @Singleton
    @FeatureDiscoveryDataStore
    fun provideFeatureDiscoveryDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.featureDiscoveryDataStore
    }
    
    @Provides
    @Singleton
    fun provideFeatureDiscoveryUseCase(
        @FeatureDiscoveryDataStore dataStore: DataStore<Preferences>
    ): FeatureDiscoveryUseCase {
        return FeatureDiscoveryUseCase(dataStore)
    }
    
    /*
    @Provides
    @Singleton
    fun provideEnhancedFeatureDiscoveryUseCase(
        dataStore: DataStore<Preferences>,
        analyticsHelper: AnalyticsHelper
    ): EnhancedFeatureDiscoveryUseCase {
        return EnhancedFeatureDiscoveryUseCase(dataStore, analyticsHelper)
    }
    */
}
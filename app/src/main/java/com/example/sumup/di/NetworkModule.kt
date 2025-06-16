package com.example.sumup.di

import com.example.sumup.BuildConfig
import com.example.sumup.data.remote.api.GeminiApiService
import com.example.sumup.data.remote.api.MockGeminiApiService
import com.example.sumup.data.remote.api.RealGeminiApiService
import com.example.sumup.data.remote.api.EnhancedGeminiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApiService(
        retrofit: Retrofit,
        apiKeyManager: com.example.sumup.utils.ApiKeyManager
    ): GeminiApiService {
        val apiKey = apiKeyManager.getGeminiApiKey()
        
        return if (apiKeyManager.hasValidApiKey()) {
            // Use enhanced API with retry logic when valid key is provided
            EnhancedGeminiApiService(retrofit, apiKey)
        } else {
            // Use mock implementation for development/demo
            MockGeminiApiService()
        }
    }
}
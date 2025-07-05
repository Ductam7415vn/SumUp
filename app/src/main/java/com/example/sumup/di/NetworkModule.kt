package com.example.sumup.di

import android.content.Context
import com.example.sumup.BuildConfig
import com.example.sumup.data.remote.api.GeminiApiService
import com.example.sumup.data.remote.mock.MockGeminiApiService
import com.example.sumup.data.remote.api.RealGeminiApiService
import com.example.sumup.data.remote.api.EnhancedGeminiApiService
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.inject.Named
import com.example.sumup.domain.model.ServiceType
import com.example.sumup.domain.model.ServiceInfo
import android.content.SharedPreferences
import com.example.sumup.utils.EnhancedApiKeyManager
import com.example.sumup.utils.migration.ApiKeyMigration

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideChuckerCollector(@ApplicationContext context: Context): ChuckerCollector {
        return ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
    }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context,
        collector: ChuckerCollector
    ): ChuckerInterceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(collector)
            .maxContentLength(250_000L)
            .redactHeaders("Authorization", "Bearer")
            .alwaysReadResponseBody(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)
            
        // Add Certificate Pinning for production
        if (!BuildConfig.DEBUG) {
            val certificatePinner = okhttp3.CertificatePinner.Builder()
                // Google's Gemini API certificate pins (you need to get actual pins)
                .add("generativelanguage.googleapis.com", "sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc=")
                .add("generativelanguage.googleapis.com", "sha256/4ZaGJlQW0+4g2B5oPNwGOCugeqPwAOL4Ob86suKC7lI=")
                .add("*.googleapis.com", "sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc=")
                .build()
            
            builder.certificatePinner(certificatePinner)
        }
        
        // Only add logging interceptors in debug builds
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor { message ->
                android.util.Log.d("OkHttp", message)
            }
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            
            builder.addInterceptor { chain ->
                val request = chain.request()
                android.util.Log.d("NetworkModule", "=== HTTP REQUEST ===")
                android.util.Log.d("NetworkModule", "URL: ${request.url}")
                android.util.Log.d("NetworkModule", "Method: ${request.method}")
                android.util.Log.d("NetworkModule", "Headers: ${request.headers}")
                
                val startTime = System.currentTimeMillis()
                try {
                    val response = chain.proceed(request)
                    val duration = System.currentTimeMillis() - startTime
                    android.util.Log.d("NetworkModule", "=== HTTP RESPONSE ===")
                    android.util.Log.d("NetworkModule", "Code: ${response.code}")
                    android.util.Log.d("NetworkModule", "Duration: ${duration}ms")
                    response
                } catch (e: Exception) {
                    val duration = System.currentTimeMillis() - startTime
                    android.util.Log.e("NetworkModule", "=== HTTP REQUEST FAILED ===")
                    android.util.Log.e("NetworkModule", "Duration: ${duration}ms")
                    android.util.Log.e("NetworkModule", "Error: ${e.message}")
                    throw e
                }
            }
            .addInterceptor(logging)
        }
        
        // Add caching
        val cacheSize = 10 * 1024 * 1024L // 10MB
        val cache = okhttp3.Cache(context.cacheDir.resolve("http_cache"), cacheSize)
        builder.cache(cache)
        
        // Add cache control interceptor
        builder.addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            
            // Cache successful responses for 5 minutes
            if (response.isSuccessful) {
                val maxAge = 300 // 5 minutes
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .build()
            } else {
                response
            }
        }
        
        // Add offline interceptor
        builder.addInterceptor { chain ->
            var request = chain.request()
            
            if (!isNetworkAvailable(context)) {
                val maxStale = 60 * 60 * 24 * 7 // 1 week
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
            }
            
            chain.proceed(request)
        }
        
        return builder
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
    fun provideApiUsageTracker(
        sharedPreferences: SharedPreferences
    ): com.example.sumup.utils.ApiUsageTracker {
        return com.example.sumup.utils.ApiUsageTracker(sharedPreferences)
    }
    
    @Provides
    @Singleton
    fun provideGeminiApiService(
        retrofit: Retrofit,
        enhancedApiKeyManager: EnhancedApiKeyManager,
        apiUsageTracker: com.example.sumup.utils.ApiUsageTracker
    ): GeminiApiService {
        val activeKey = enhancedApiKeyManager.getActiveApiKey()
        val hasValidKey = activeKey != null
        
        android.util.Log.d("NetworkModule", "=== API SERVICE SETUP ===")
        android.util.Log.d("NetworkModule", "API Key present: ${activeKey != null}")
        android.util.Log.d("NetworkModule", "API Key valid: $hasValidKey")
        if (activeKey != null) {
            android.util.Log.d("NetworkModule", "API Key (first 10 chars): ${activeKey.take(10)}...")
        }
        
        // Force using mock service for testing
        val forceMock = false // Using real API for testing
        
        return if (hasValidKey && activeKey != null && !forceMock) {
            // Use enhanced API with retry logic when valid key is provided
            android.util.Log.d("NetworkModule", "Using REAL Gemini API Service (EnhancedGeminiApiService)")
            EnhancedGeminiApiService(retrofit, activeKey, enhancedApiKeyManager, apiUsageTracker)
        } else {
            // Use mock implementation for development/demo
            android.util.Log.d("NetworkModule", "Using MOCK Gemini API Service (forceMock=$forceMock)")
            MockGeminiApiService()
        }
    }
    
    @Provides
    @Singleton
    @Named("real")
    fun provideRealGeminiApiService(
        retrofit: Retrofit
    ): GeminiApiService {
        // Always provide real API service for validation
        // This will use whatever key is passed to generateContent method
        return retrofit.create(GeminiApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideProcessLargePdfUseCase(
        pdfRepository: com.example.sumup.domain.repository.PdfRepository,
        summaryRepository: com.example.sumup.domain.repository.SummaryRepository,
        summarizeTextUseCase: com.example.sumup.domain.usecase.SummarizeTextUseCase
    ): com.example.sumup.domain.usecase.ProcessLargePdfUseCase {
        return com.example.sumup.domain.usecase.ProcessLargePdfUseCase(
            pdfRepository,
            summaryRepository,
            summarizeTextUseCase
        )
    }

    @Provides
    @Singleton
    fun provideServiceInfo(
        enhancedApiKeyManager: EnhancedApiKeyManager
    ): ServiceInfo {
        val hasValidKey = enhancedApiKeyManager.getActiveApiKey() != null
        val forceMock = false // Using same logic as in provideGeminiApiService
        
        return if (hasValidKey && !forceMock) {
            ServiceInfo(type = ServiceType.REAL_API)
        } else {
            ServiceInfo(type = ServiceType.MOCK_API)
        }
    }
    
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
    
    @Provides
    @Singleton
    fun provideEnhancedApiKeyManager(
        sharedPreferences: SharedPreferences
    ): EnhancedApiKeyManager {
        return EnhancedApiKeyManager(sharedPreferences)
    }
}
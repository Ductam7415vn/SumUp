package com.example.sumup.di

import com.example.sumup.utils.analytics.AnalyticsHelper
import com.example.sumup.utils.analytics.AnalyticsHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    
    @Binds
    @Singleton
    abstract fun bindAnalyticsHelper(
        analyticsHelperImpl: AnalyticsHelperImpl
    ): AnalyticsHelper
}
package com.example.sumup.di

import com.example.sumup.data.repository.PdfRepositoryImpl
import com.example.sumup.data.repository.SettingsRepositoryImpl
import com.example.sumup.data.repository.StreamingSummaryRepositoryImpl
import com.example.sumup.data.repository.SummaryRepositoryImpl
import com.example.sumup.domain.repository.PdfRepository
import com.example.sumup.domain.repository.SettingsRepository
import com.example.sumup.domain.repository.StreamingSummaryRepository
import com.example.sumup.domain.repository.SummaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSummaryRepository(
        summaryRepositoryImpl: SummaryRepositoryImpl
    ): SummaryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
    
    @Binds
    @Singleton
    abstract fun bindPdfRepository(
        pdfRepositoryImpl: PdfRepositoryImpl
    ): PdfRepository
    
    @Binds
    @Singleton
    abstract fun bindStreamingSummaryRepository(
        streamingSummaryRepositoryImpl: StreamingSummaryRepositoryImpl
    ): StreamingSummaryRepository
}
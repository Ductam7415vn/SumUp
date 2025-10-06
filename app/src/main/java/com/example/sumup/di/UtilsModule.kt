package com.example.sumup.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.sumup.utils.clipboard.ClipboardManager
import com.example.sumup.utils.clipboard.ClipboardManagerImpl
import com.example.sumup.utils.drafts.DraftManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {
    
    @Binds
    @Singleton
    abstract fun bindClipboardManager(
        clipboardManagerImpl: ClipboardManagerImpl
    ): ClipboardManager
    
    companion object {
        @Provides
        @Singleton
        fun provideDraftManager(
            @ApplicationContext context: Context
        ): DraftManager = DraftManager(context)
        
        @Provides
        @Singleton
        fun provideInputValidator(): com.example.sumup.utils.InputValidator = com.example.sumup.utils.InputValidator
        
        @Provides
        @Singleton
        fun provideSharedPreferences(
            @ApplicationContext context: Context
        ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        
        @Provides
        @Singleton
        fun provideApiKeyMigration(
            @ApplicationContext context: Context,
            sharedPreferences: SharedPreferences,
            enhancedApiKeyManager: com.example.sumup.utils.EnhancedApiKeyManager
        ): com.example.sumup.utils.migration.ApiKeyMigration = com.example.sumup.utils.migration.ApiKeyMigration(
            context,
            enhancedApiKeyManager,
            sharedPreferences
        )

        // Export functionality providers
        @Provides
        @Singleton
        fun provideTextExporter(): com.example.sumup.domain.usecase.TextExporter {
            return com.example.sumup.domain.usecase.TextExporter()
        }

        @Provides
        @Singleton
        fun provideMarkdownExporter(): com.example.sumup.domain.usecase.MarkdownExporter {
            return com.example.sumup.domain.usecase.MarkdownExporter()
        }

        @Provides
        @Singleton
        fun providePdfExporter(
            @ApplicationContext context: Context
        ): com.example.sumup.domain.usecase.PdfExporter {
            return com.example.sumup.domain.usecase.PdfExporter(context)
        }

        @Provides
        @Singleton
        fun provideExportSummaryUseCase(
            @ApplicationContext context: Context,
            pdfExporter: com.example.sumup.domain.usecase.PdfExporter,
            markdownExporter: com.example.sumup.domain.usecase.MarkdownExporter,
            textExporter: com.example.sumup.domain.usecase.TextExporter
        ): com.example.sumup.domain.usecase.ExportSummaryUseCase {
            return com.example.sumup.domain.usecase.ExportSummaryUseCase(
                context,
                pdfExporter,
                markdownExporter,
                textExporter
            )
        }
    }
}
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
    }
}
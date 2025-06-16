package com.example.sumup.di

import android.content.Context
import com.example.sumup.utils.clipboard.ClipboardManager
import com.example.sumup.utils.clipboard.ClipboardManagerImpl
import com.example.sumup.utils.drafts.DraftManager
import com.example.sumup.utils.InputValidator
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
        fun provideInputValidator(): InputValidator = InputValidator()
    }
}
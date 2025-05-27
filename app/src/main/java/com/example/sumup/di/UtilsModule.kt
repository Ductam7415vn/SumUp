package com.example.sumup.di

import com.example.sumup.utils.clipboard.ClipboardManager
import com.example.sumup.utils.clipboard.ClipboardManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}
package com.example.sumup

import android.app.Application
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SumUpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize PDFBox Android resources
        PDFBoxResourceLoader.init(applicationContext)
    }
}
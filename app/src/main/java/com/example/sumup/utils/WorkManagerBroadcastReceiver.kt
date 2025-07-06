package com.example.sumup.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Broadcast receiver for handling WorkManager actions from notifications
 */
@AndroidEntryPoint
class WorkManagerBroadcastReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var workManagerHelper: WorkManagerHelper
    
    @Inject
    lateinit var notificationHelper: NotificationHelper
    
    override fun onReceive(context: Context, intent: Intent) {
        val documentId = intent.getStringExtra("documentId") ?: return
        
        when (intent.action) {
            NotificationHelper.ACTION_CANCEL -> {
                // Cancel the work
                workManagerHelper.cancelSummarization(documentId)
                
                // Cancel the notification
                notificationHelper.cancelNotification(documentId)
            }
            NotificationHelper.ACTION_PAUSE -> {
                // TODO: Implement pause functionality in Phase 4
                android.util.Log.d("WorkManagerBroadcastReceiver", "Pause requested for $documentId")
            }
        }
    }
}
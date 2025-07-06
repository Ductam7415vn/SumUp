package com.example.sumup.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.sumup.MainActivity
import com.example.sumup.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for managing notifications
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "sumup_summarization"
        const val CHANNEL_NAME = "Document Summarization"
        const val CHANNEL_DESCRIPTION = "Shows progress of document summarization"
        
        const val ACTION_PAUSE = "com.example.sumup.ACTION_PAUSE"
        const val ACTION_CANCEL = "com.example.sumup.ACTION_CANCEL"
        const val ACTION_VIEW = "com.example.sumup.ACTION_VIEW"
        
        const val NOTIFICATION_ID_BASE = 1000
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = CHANNEL_DESCRIPTION
                setShowBadge(false)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showProgressNotification(
        documentId: String,
        title: String,
        progress: Int,
        currentSection: Int,
        totalSections: Int,
        isIndeterminate: Boolean = false
    ) {
        val notificationId = documentId.hashCode() + NOTIFICATION_ID_BASE
        
        val contentText = if (totalSections > 0) {
            "Processing section $currentSection of $totalSections"
        } else {
            "Processing document..."
        }
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sumup_logo)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, isIndeterminate)
            .setContentIntent(getViewPendingIntent(documentId))
        
        // Add cancel action
        builder.addAction(
            android.R.drawable.ic_delete,
            "Cancel",
            getCancelPendingIntent(documentId)
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }
        
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
    
    fun showCompletionNotification(
        documentId: String,
        title: String,
        message: String
    ) {
        val notificationId = documentId.hashCode() + NOTIFICATION_ID_BASE
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sumup_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getViewPendingIntent(documentId))
        
        // Add view action
        builder.addAction(
            android.R.drawable.ic_menu_view,
            "View Summary",
            getViewPendingIntent(documentId)
        )
        
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
    
    fun showErrorNotification(
        documentId: String,
        title: String,
        message: String
    ) {
        val notificationId = documentId.hashCode() + NOTIFICATION_ID_BASE
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }
    
    fun cancelNotification(documentId: String) {
        val notificationId = documentId.hashCode() + NOTIFICATION_ID_BASE
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
    
    private fun getViewPendingIntent(documentId: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("summaryId", documentId)
            putExtra("navigateTo", "result")
        }
        
        return PendingIntent.getActivity(
            context,
            documentId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun getCancelPendingIntent(documentId: String): PendingIntent {
        val intent = Intent(context, WorkManagerBroadcastReceiver::class.java).apply {
            action = ACTION_CANCEL
            putExtra("documentId", documentId)
        }
        
        return PendingIntent.getBroadcast(
            context,
            documentId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
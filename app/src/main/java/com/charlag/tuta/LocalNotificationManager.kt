package com.charlag.tuta

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.charlag.tuta.compose.ComposeActivity
import com.charlag.tuta.notifications.isAtLeastOreo
import javax.inject.Inject
import kotlin.random.Random

class LocalNotificationManager @Inject constructor(
    private val context: Context
) {
    private val notificationManager = context.getSystemService<NotificationManager>()!!

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (isAtLeastOreo()) {
            val miscChannel = NotificationChannel(
                MISC_NOTIFICATION_CHANNEL_ID,
                "Misc",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(miscChannel)
        }
    }

    /**
     * @return Notificaiton id (to cancel it later)
     */
    fun showSendingNotification(): Int {
        val notification = NotificationCompat.Builder(context, MISC_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_send_black_24dp)
            .setContentTitle("Sending mail")
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setProgress(0, 0, true)
            .setOngoing(true)
            .build()
        val id = (Math.random() * 100_000).toInt()
        notificationManager.notify(id, notification)
        return id
    }

    fun hideSendingNotification(id: Int) {
        notificationManager.cancel(id)
    }

    fun showFailedToSendNotification(localDraftId: Long) {
        val intent = Intent(context, ComposeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(ComposeActivity.LOCAL_DRAFT_EXTRA, localDraftId)
        }
        val notification = NotificationCompat.Builder(context, MISC_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_send_black_24dp)
            .setContentTitle("Failed to send mail")
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    Random.nextInt(), // new one so that extras don't clash
                    intent,
                    0
                )
            )
            .setAutoCancel(true)
            .build()
        val id = (Math.random() * 100_000).toInt()
        notificationManager.notify(id, notification)
    }

    companion object {
        private const val MISC_NOTIFICATION_CHANNEL_ID = "misc"
    }
}
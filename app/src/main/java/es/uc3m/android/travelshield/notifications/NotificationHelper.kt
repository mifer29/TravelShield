package es.uc3m.android.travelshield.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.uc3m.android.travelshield.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val STANDARD_CHANNEL_ID = "standard_channel"
        const val STANDARD_CHANNEL_NAME = "Standard Notifications"
        const val STANDARD_CHANNEL_DESCRIPTION = "Channel for general notifications"

        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val standardChannel = NotificationChannel(
                STANDARD_CHANNEL_ID,
                STANDARD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = STANDARD_CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(standardChannel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, STANDARD_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())  // Add pending intent for notification click

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}

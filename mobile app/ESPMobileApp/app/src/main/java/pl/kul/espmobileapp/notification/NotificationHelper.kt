package pl.kul.espmobileapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import pl.kul.espmobileapp.MainActivity
import pl.kul.espmobileapp.R

@RequiresApi(Build.VERSION_CODES.O)
class NotificationHelper(private val context: Context) {
    private val channelId = "motion_alerts_channel"
    private val notificationId = 1
    private val intent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    private val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = R.string.motion_motification_channel_name
        val descriptionText = R.string.motion_notification_channel_description
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, name.toString(), importance).apply {
            description = descriptionText.toString()
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification() {
        val title = R.string.motion_alert_title
        val description = R.string.motion_alert_description

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(context, title))
            .setContentText(getString(context, description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
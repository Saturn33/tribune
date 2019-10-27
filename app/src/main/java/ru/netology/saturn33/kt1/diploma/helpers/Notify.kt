package ru.netology.saturn33.kt1.diploma.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.netology.saturn33.kt1.diploma.R

private const val SIMPLE_NOTIFY_ID = 0
private const val SIMPLE_NOTIFY_CHANNEL_ID = "simple_channel"

object Notify {
    private var channelCreated = false

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Реакции"
            val descriptionText = "Уведомления о реакциях на Ваши идеи"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(SIMPLE_NOTIFY_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannelIfNotCreated(context: Context) {
        if (!channelCreated)
            createNotificationChannel(context)
        channelCreated = true
    }

    fun simpleNotification(
        context: Context,
        title: String,
        text: String,
        nId: Int = SIMPLE_NOTIFY_ID
    ) {
        createNotificationChannelIfNotCreated(context)
        val builder = NotificationCompat.Builder(context, SIMPLE_NOTIFY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.priority = NotificationManager.IMPORTANCE_HIGH
        }
        NotificationManagerCompat.from(context).notify(nId, builder.build())
    }

}

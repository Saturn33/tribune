package ru.netology.saturn33.kt1.diploma.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.ui.FeedActivity
import ru.netology.saturn33.kt1.diploma.ui.ReactionListActivity

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
        postId: Long,
        title: String,
        text: String,
        nId: Int = SIMPLE_NOTIFY_ID
    ) {
        val intent = if (postId > 0) {
            Intent(context, ReactionListActivity::class.java).apply {
                putExtra("postId", postId)
                putExtra("createNewFeed", true)
            }
        } else {
            Intent(context, FeedActivity::class.java)
        }
        val pendingIntent = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        createNotificationChannelIfNotCreated(context)
        val builder = NotificationCompat.Builder(context, SIMPLE_NOTIFY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.priority = NotificationManager.IMPORTANCE_HIGH
        }
        NotificationManagerCompat.from(context).notify(nId, builder.build())
    }

}

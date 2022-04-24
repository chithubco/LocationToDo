package com.udacity.project4.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.udacity.project4.MainActivity
import com.udacity.project4.R
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.utils.Constants.CHANNEL_ID
import com.udacity.project4.utils.Constants.NOTIFICATION_ID

class NotificationHelper(private val context: Context) {

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel
            val name = CHANNEL_ID
            val descriptionText = "Todo App Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(reminder: Reminder) {
        createNotificationChannel()
        // Create an Intent
//        val intent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
        val bundle = Bundle()
        bundle.putParcelable(
            "reminder",
            Reminder(
                1,
                reminder.title,
                reminder.description,
                reminder.latitude,
                reminder.longitude,
                reminder.createdDate
            )
        )
        val intent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.listFragment)
            .setArguments(bundle)
            .createPendingIntent()

//        intent.putExtra("title",title)
//        intent.putExtra("description",description)
//        val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_map)
        val title = reminder.title
        val description = reminder.description
        // Create Notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_map)
            .setLargeIcon(icon)
            .setContentTitle("Reminder : $title")
            .setContentText("Description : $description")
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigLargeIcon(null)
            )
            .setContentIntent(intent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_location, "Do Something", intent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
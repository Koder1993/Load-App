package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    context: Context,
    channelId: String,
    message: String,
    downloadStatus: String,
    fileName: String
) {

    val activityLaunchIntent = Intent(context, DetailActivity::class.java)
    activityLaunchIntent.putExtra(Constants.KEY_STATUS_DOWNLOAD, downloadStatus)
    activityLaunchIntent.putExtra(Constants.KEY_FILE_NAME, fileName)

    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        activityLaunchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),
            pendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}
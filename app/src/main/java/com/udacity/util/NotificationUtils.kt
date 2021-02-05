package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.udacity.R

// Notification ID.
private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
	messageBody: String,
	applicationContext: Context,
	actionIntent: PendingIntent
) {
	val detailImage =
		AppCompatResources.getDrawable(applicationContext, R.drawable.ic_assistant_black_24dp)
			?.toBitmap()

	val bigPicStyle = NotificationCompat.BigPictureStyle()
		.bigPicture(detailImage)
		.bigLargeIcon(null)

	val builder = NotificationCompat.Builder(
		applicationContext,
		applicationContext.getString(R.string.loadapp_notification_channel_id)
	).setSmallIcon(R.drawable.ic_assistant_black_24dp)
		.setContentTitle(
			applicationContext
				.getString(R.string.notification_title)
		)
		.setContentText(messageBody)
		.setPriority(NotificationCompat.PRIORITY_HIGH)
		.setContentIntent(actionIntent)
		.setAutoCancel(true)
		.setLargeIcon(detailImage)
		.setStyle(bigPicStyle)
		.addAction(
			R.drawable.ic_assistant_black_24dp,
			applicationContext.getString(R.string.notification_button),
			actionIntent
		)

	notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
	cancelAll()
}
package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.cancelNotifications
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

	private var downloadID: Long = 0
	private var downloadUrl: String = ""
	private var downloadFile: String = ""
	private val REQUEST_CODE = 0

	private lateinit var notificationManager: NotificationManager
	private lateinit var pendingIntent: PendingIntent
	private lateinit var action: NotificationCompat.Action

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		notificationManager = ContextCompat.getSystemService(
			application,
			NotificationManager::class.java
		) as NotificationManager

		createChannel(
			getString(R.string.loadapp_notification_channel_id),
			getString(R.string.loadapp_notification_channel_name)
		)

		registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


		custom_button.setOnClickListener {
			custom_button.setState(ButtonState.Clicked)
			notificationManager.cancelNotifications()
			if (!radio_glide.isChecked && !radio_udacity.isChecked && !radio_retrofit.isChecked) {
				Toast.makeText(
					it.context,
					it.context.getString(R.string.selection_message),
					Toast.LENGTH_SHORT
				).show()
			} else {
				download()
			}
		}

	}

	private val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

			val status: String = when (downloadID == id) {
				true -> "Success"
				false -> "Failed"
			}
			val detailIntent = Intent(applicationContext, DetailActivity::class.java)
			detailIntent.putExtra("url", downloadUrl)
			detailIntent.putExtra("status", status)
			val detailPendingIntent: PendingIntent = PendingIntent.getActivity(
				applicationContext,
				REQUEST_CODE,
				detailIntent,
				PendingIntent.FLAG_UPDATE_CURRENT
			)

			if (downloadID == id) {
				Toast.makeText(applicationContext, "Download Completed", Toast.LENGTH_SHORT).show();
				notificationManager.sendNotification(
					downloadFile + " is downloaded!",
					application,
					detailPendingIntent
				)
			} else {
				Toast.makeText(applicationContext, "Download Failed", Toast.LENGTH_SHORT).show();
				notificationManager.sendNotification(
					downloadFile + " download failed",
					application,
					detailPendingIntent
				)
			}
			custom_button.setState(ButtonState.Completed)
		}
	}

	private fun download() {
		custom_button.setState(ButtonState.Loading)
		val request =
			DownloadManager.Request(Uri.parse(downloadUrl))
				.setTitle(getString(R.string.app_name))
				.setDescription(getString(R.string.app_description))
				.setRequiresCharging(false)
				.setAllowedOverMetered(true)
				.setAllowedOverRoaming(true)

		val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
		downloadID =
			downloadManager.enqueue(request)// enqueue puts the download request in the queue.

	}

	companion object {
		private const val GLIDE_URL =
			"https://github.com/bumptech/glide"
		private const val UDACITY_URL =
			"https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
		private const val RETROFIT_URL =
			"https://github.com/square/retrofit"
	}

	fun onRadioButtonClicked(view: View) {
		if (view is RadioButton) {
			// Is the button now checked?
			val checked = view.isChecked
			// Check which radio button was clicked
			when (view.getId()) {
				R.id.radio_glide ->
					if (checked) {
						downloadUrl = GLIDE_URL
						downloadFile = getString(R.string.bumptech_glide_downloaded)
					}
				R.id.radio_udacity ->
					if (checked) {
						downloadUrl = UDACITY_URL
						downloadFile = getString(R.string.project_starter_download)
					}
				R.id.radio_retrofit ->
					if (checked) {
						downloadUrl = RETROFIT_URL
						downloadFile = getString(R.string.square_retrofit_downloaded)
					}
			}
		}
	}

	private fun createChannel(channelId: String, channelName: String) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationChannel = NotificationChannel(
				channelId,
				channelName,
				NotificationManager.IMPORTANCE_LOW
			)

			notificationChannel.enableLights(true)
			notificationChannel.lightColor = Color.RED
			notificationChannel.enableVibration(true)
			notificationChannel.description = downloadFile

			val notificationManager = getSystemService(
				NotificationManager::class.java
			)
			notificationManager.createNotificationChannel(notificationChannel)

		}

	}

}

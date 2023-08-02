package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.RadioOption
import com.udacity.util.sendNotification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private var selectedRadioOption: RadioOption = RadioOption.NOT_SELECTED

    companion object {
        private const val CHANNEL_ID = "channelId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createNotificationChannel(CHANNEL_ID, getString(R.string.notification_channel))

        binding.contentMainLayout.customButton.setOnClickListener {
            selectedRadioOption = getSelectedRadioOption()
            if (selectedRadioOption == RadioOption.NOT_SELECTED) {
                Toast.makeText(
                    this, getString(R.string.select_file_download_text), Toast.LENGTH_SHORT
                ).show()
                binding.contentMainLayout.customButton.updateButtonState(ButtonState.Clicked)
            } else {
                binding.contentMainLayout.customButton.updateButtonState(ButtonState.Loading)
                download(selectedRadioOption.url)
            }
        }
    }

    private fun getSelectedRadioOption(): RadioOption {
        return when (binding.contentMainLayout.radioGroup.checkedRadioButtonId) {
            binding.contentMainLayout.glide.id -> RadioOption.GLIDE
            binding.contentMainLayout.loadApp.id -> RadioOption.LOAD_APP
            binding.contentMainLayout.retrofit.id -> RadioOption.RETROFIT
            else -> RadioOption.NOT_SELECTED
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query()
            query.setFilterById(id!!)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (columnIndex >= 0) {
                    val resultStatus = cursor.getInt(columnIndex)
                    val downloadStatus =
                        if (resultStatus == DownloadManager.STATUS_SUCCESSFUL) "Success" else "Fail"
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.notification_description),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.contentMainLayout.customButton.updateButtonState(ButtonState.Completed)
                    val notificationManager = ContextCompat.getSystemService(
                        applicationContext,
                        NotificationManager::class.java
                    )
                    notificationManager?.sendNotification(
                        applicationContext,
                        CHANNEL_ID,
                        getString(R.string.notification_description),
                        downloadStatus,
                        getString(selectedRadioOption.fileNamePath)
                    )
                }
            }
        }
    }

    private fun download(url: String) {
        val request = DownloadManager.Request(Uri.parse(url)).setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.app_description)).setRequiresCharging(false)
            .setAllowedOverMetered(true).setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = getString(R.string.notification_channel)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
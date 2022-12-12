package com.example.voting_machine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.cvte.maxhub.log.NLog
import com.maxhub.os.flutter_multi_windows.WindowService

class VotingService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForeground()
        FlutterWindowServiceManager.init(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        NLog.i(TAG, "onStartCommand: intent=$intent, flags=$flags, startId=$startId")
        NLog.i(TAG, "==== onStartCommand: ${System.currentTimeMillis()}")
        val intent = Intent(this, WindowService::class.java).apply {
            putExtra("name", "checkAndDownload")
            putExtra("entryPoint", "androidWindow")
            putExtra("width", 300)
            putExtra("height", 450)
            putExtra("gravityX", 2)
            putExtra("gravityY", 2)
            putExtra("paddingX", 32.0)
            putExtra("paddingY", 96.0)
            putExtra("draggable", true)
            putExtra("type", "single")
        }
        FlutterWindowServiceManager.executeTask(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopForeground()
        super.onDestroy()
    }

    private fun startForeground() {
        val channelId = "Upgrade_Service"
        val channelName = "Upgrade Service"
        val notificationId = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId).build()
        startForeground(notificationId, notification)
    }

    private fun stopForeground() {
        stopForeground(true)
    }

    companion object {
        private const val TAG = "VotingService"
    }
}
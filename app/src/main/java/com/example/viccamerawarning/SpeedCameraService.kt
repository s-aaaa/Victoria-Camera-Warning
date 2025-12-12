package com.example.viccamerawarning

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import android.location.Location
import android.util.Log
import com.example.viccamerawarning.data.repository.CameraRepository
import com.example.viccamerawarning.location.DefaultLocationTracker
import com.example.viccamerawarning.location.LocationTracker
import com.example.viccamerawarning.tts.ISpeechService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SpeedCameraService : Service() {

    @Inject lateinit var repo: CameraRepository
    @Inject lateinit var locationTracker: LocationTracker
    @Inject lateinit var speaker: ISpeechService

    private var alertManager: CameraAlertManager? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())

        Log.d("viccameras", "Foreground service started!")

    }

    private fun createNotification(): Notification {
        val channelId = "speed_camera_alerts"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Speed Camera Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        Log.d("viccameras", "Camera service notification created!")
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Speed Camera Alerts")
            .setContentText("Monitoring your speed and location...")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start your location updates / monitoring here
        // Launch Collectors
        serviceScope.launch {
            val cameras = repo.getCameras()
            alertManager = CameraAlertManager(cameras)
            alertManager?.speaker = speaker

            launch{

                locationTracker.getLocationUpdates().collect { loc ->
                    Log.d("viccameras", "Location update received: $loc")
                    if (loc != null) {
                        alertManager?.updateLocation(loc)
                    }
                }
            }

            launch{
                alertManager?.events?.collect { alert ->
                    Log.d("viccameras", "SpeedCameraService Received alert: $alert")
                }
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy(){
        Log.d("viccameras", "Camera Service Destroyed!")
        super.onDestroy()
        serviceScope.cancel()
    }
}

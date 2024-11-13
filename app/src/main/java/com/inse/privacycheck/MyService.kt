package com.inse.privacycheck

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MyService : Service() {
    /*
    * Code for alarm manager
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    * */


//    override fun onCreate() {
//
////        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
////        val intent = Intent(this, MyReceiver::class.java)
////        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        super.onCreate()
//    }

    override fun onBind(intent: Intent): IBinder {
        return TODO("Provide the return value")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel("MyServiceStatus", "MyService", NotificationManager.IMPORTANCE_DEFAULT)

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }


            try{
                val builder = Notification.Builder(applicationContext, "MyServiceStatus")
                    .setContentTitle("MyService")
                    .setContentText("MyService is running")
                    .setOngoing(true)
                Log.d("MyService", "Notification Sent")

                val notification = builder.build()

                startForeground(1, notification)
                Log.d("MyService", "Service Started")

            }catch (e: Exception){

                Log.d("MyService", "Service Failed")
            }

        }


        //Alarm worker with 1 minute interval
/*
            Log.d("MyService", "Starting worker")
        val intervalMillis = 1000 // 5 seconds
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis.toLong(),
            pendingIntent
        )
*/
                //WorkManager Worker with minimum 15 minutes, using look to run for every 5 secs
//            val constraints = Constraints.Builder()
//                .build()

            val workRequest = OneTimeWorkRequestBuilder<AppInstallationWorker>()
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniqueWork("AppInstallationWorker",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        return START_STICKY
    }
}
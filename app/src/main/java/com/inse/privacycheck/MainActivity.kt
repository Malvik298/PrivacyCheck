package com.inse.privacycheck

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.WorkManager


class MainActivity : AppCompatActivity() {
    val utils = Utilities()
    val channelId = "New_App_Installations"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
                utils.savePackageNamesToFile(applicationContext)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, show the dialog box
            showNotificationPermissionRequestDialog()
        } else {
            Log.d("App Permission", "Permission Granted")
            // Permission is granted, proceed with querying installed applications
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.QUERY_ALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, show the dialog box
            showPermissionRequestDialog()
        } else {
            Log.d("App Permission", "Permission Granted")
            // Permission is granted, proceed with querying installed applications
        }
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "New App Installations", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
//
        val startServ = findViewById<Button>(R.id.startSrv)
        val stopServ = findViewById<Button>(R.id.stopSrv)
//
        startServ.setOnClickListener {
            startService(Intent(this, MyService::class.java))
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        }
//
        stopServ.setOnClickListener {
            stopService(Intent(this, MyService::class.java))
            WorkManager.getInstance(this).cancelUniqueWork("AppInstallationWorker")
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show()
        }



    }



    @SuppressLint("ObsoleteSdkInt")


    private fun showPermissionRequestDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs permission to query all installed applications to provide its functionality.")
            .setPositiveButton("Grant Permission") { _, _ ->
                // Request the permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.QUERY_ALL_PACKAGES), 1)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showNotificationPermissionRequestDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs permission to show notifications to provide its functionality.")
            .setPositiveButton("Grant Permission") { _, _ ->
                // Request the permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with querying installed applications
            } else {
                // Permission denied, handle accordingly (e.g., show an error message)
                // ...
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MyService::class.java))
    }
}
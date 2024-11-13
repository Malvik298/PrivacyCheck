package com.inse.privacycheck

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import java.io.File
import java.security.MessageDigest

class Utilities {

    fun savePackageNamesToFile(context: Context) {

        val packageManager = context.applicationContext.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

//        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

//        val resolvedInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            pm.queryIntentActivities(
//                mainIntent,
//                PackageManager.ResolveInfoFlags.of(0L)
//            )
//        } else {
//            pm.queryIntentActivities(mainIntent, 0)
//        }


        val packageNames = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .map { it.packageName }
            .sorted()
//        Log.d("AppInstallationWorker", "pNames: ${resolvedInfos.size}")

        Log.d("AppInstallationWorker", "Saving package names: ${packageNames.size}")
        val file = File(context.filesDir, "package_names.txt")
        file.writeText(packageNames.joinToString("\n"))

        // Calculate and store the hash
        val hash = calculateHash(packageNames)
        saveHash(context, hash)
    }

    private fun calculateHash(packageNames: List<String>): String {
        // Use a suitable hashing algorithm (e.g., SHA-256)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val inputBytes = packageNames.joinToString("").toByteArray(Charsets.UTF_8)
        val hashBytes = messageDigest.digest(inputBytes)
        return hashBytes.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun saveHash(context: Context, hash: String) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit { putString("package_hash", hash) }
    }

    fun checkForNewApps(context: Context):List<String> {
        val packageManager = context.applicationContext.packageManager
        var newApps:List<String> = emptyList<String>()
        var currentPackageNames = emptyList<String>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        currentPackageNames = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .map { it.packageName }
            .sorted()

        Log.d("AppInstallationWorker", "Current package Size: ${currentPackageNames.size}")
        val storedHash = getStoredHash(context)
        val currentHash = calculateHash(currentPackageNames)
        Log.d("AppInstallationWorker", "Stored Hash: $storedHash, Current Hash: $currentHash")
        if (storedHash != currentHash) {
            val oldPackageNames = getStoredPackageNames(context)
            // Changes detected, find new apps
            Log.d("AppInstallationWorker", "Old package Size: ${oldPackageNames.size}")
            newApps = currentPackageNames.minus(oldPackageNames)
            Log.d("AppInstallationWorker", "New package: ${newApps}")
            // Update stored data and hash
            if(newApps.size != 0){
                showNotification(context, newApps.joinToString(", "))
            }
            savePackageNamesToFile(context)

            // Send notification for new apps
            return newApps
        }
        return emptyList()
    }

    private fun showNotification(context: Context, packageName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "New_App_Installations"

        // Create the notification channel (for Android Oreo and above)

        Log.d("AppInstallationWorker", "New package installed: $packageName")
        // Create the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("New App Installed")
            .setContentText("A new app has been installed: $packageName")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }

    private fun getStoredHash(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("package_hash", "") ?: ""
    }

    private fun getStoredPackageNames(context: Context): List<String> {
        val file = File(context.filesDir, "package_names.txt")
        return if (file.exists()) file.readLines() else emptyList()
    }
}
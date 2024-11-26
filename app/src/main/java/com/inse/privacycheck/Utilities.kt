package com.inse.privacycheck

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
//import androidx.privacysandbox.tools.core.generator.build
import okhttp3.*;
import okio.IOException
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Utilities {

    fun savePackageNamesToFile(context: Context) {

        val packageManager = context.applicationContext.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

//        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)




        val packageNames = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .map { it.packageName }
            .sorted()
//        Log.d("AppInstallationWorker", "pNames: ${resolvedInfos.size}")

//        Log.d("AppInstallationWorker", "Saving package names: ${packageNames.size}")
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

//        Log.d("AppInstallationWorker", "Current package Size: ${currentPackageNames.size}")
        val storedHash = getStoredHash(context)
        val currentHash = calculateHash(currentPackageNames)
//        Log.d("AppInstallationWorker", "Stored Hash: $storedHash, Current Hash: $currentHash")
        if (storedHash != currentHash) {
            val oldPackageNames = getStoredPackageNames(context)
            // Changes detected, find new apps
//            Log.d("AppInstallationWorker", "Old package Size: ${oldPackageNames.size}")
            newApps = currentPackageNames.minus(oldPackageNames)
//            Log.d("AppInstallationWorker", "New package: ${newApps}")
            // Update stored data and hash
            if(newApps.size != 0){
                showNotification(context, newApps.joinToString(", "))
            }
            savePackageNamesToFile(context)
            for(appName in newApps)
            {
                callAPI(appName, context)
            }
            // Send notification for new apps
            return newApps
        }
        return emptyList()
    }

    private fun showNotification(context: Context, packageName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "New_App_Installations"



        // Create the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("New App Installed")
            .setContentText("A new app has been installed: $packageName")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }

    private fun showPrivacyNotification(context: Context, packageName: String, text: String, pendingIntent: PendingIntent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "Privacy_analysis"
        Log.d("Notification P", "Notification sent Privacy")


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Policy Analysis")
            .setContentText("Rating Overall for $packageName: $text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .build()
        var random = Random.nextInt(1000000000)
        // Show the notification
        notificationManager.notify(random, notification)
    }

    private fun getStoredHash(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("package_hash", "") ?: ""
    }

    private fun getStoredPackageNames(context: Context): List<String> {
        val file = File(context.filesDir, "package_names.txt")
        return if (file.exists()) file.readLines() else emptyList()
    }

    fun callAPI(queryParam: String, context: Context) {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.SECONDS)
            .build()


        val request = Request.Builder()
            .url("https://nn.i.spectresudo.com/webhook/app-request?app=${queryParam}")
            .build()


        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("API", "Failed, ${e.toString()}")

            }

            override fun onResponse(call: Call, response: Response) {
                val body =  JSONObject(response.body?.string().toString())
//                Log.d("Response received", "${body")
                if(response.code == 200){
                    val privacyDetails = body.getJSONArray("Privacy_Analysis")
                    val overall_score = body.get("overall_score")
                    val intent = Intent(context, PrivacyDetailsActivity::class.java)
                    intent.putExtra("privacyDetails", privacyDetails.toString())
                    Log.d("Response Score Received ",overall_score.toString())
                    Log.d("Response Privacy Received ",privacyDetails.toString())
                    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    showPrivacyNotification(context, queryParam, overall_score.toString(), pendingIntent )
                } else
                {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val notification = NotificationCompat.Builder(context, "Privacy_analysis")
                        .setContentTitle("Policy Analysis")
                        .setContentText("Policy Not Found on PlayStore")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .build()
                    notificationManager.notify(Random.nextInt(100000), notification)
                }

            }
        })

    }

}
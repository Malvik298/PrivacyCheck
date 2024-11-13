package com.inse.privacycheck


import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class AppInstallationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        while(true){
            if(isStopped) break
            val utils = Utilities()
            utils.checkForNewApps(applicationContext)
            Log.d("AppInstallationWorker", "Work Started")

            Thread.sleep(5000)

            Log.d("AppInstallationWorker", "Work Started + 5 sec")

        }
        Log.d("AppInstallationWorker", "Work Stopped")
        return Result.success()
    }



}
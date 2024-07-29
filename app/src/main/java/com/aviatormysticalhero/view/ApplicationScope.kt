package com.aviatormysticalhero.view

import android.app.ActivityManager
import android.app.Application
import android.os.Process

class ApplicationScope : Application() {
  override fun onCreate() {
    super.onCreate()
    val processId = Process.myPid()
    val activityManager =
      applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
    var isMainAppProcess = true
    try {
      for (processInfo in activityManager.runningAppProcesses) {
        if (processInfo.pid == processId && processInfo.processName != null && (processInfo.processName.endsWith(
            ":acra"
          ) || processInfo.processName.endsWith(":outbox"))
        ) {
          isMainAppProcess = false
          break
        }
      }
    } catch (e: Exception) {
    }
    if (!isMainAppProcess) {
      throw RuntimeException("We're in a buddybuild process but ended up in main app code. This is bad!")
    }
  }
}
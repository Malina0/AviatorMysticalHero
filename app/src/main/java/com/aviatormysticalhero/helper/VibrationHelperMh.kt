package com.aviatormysticalhero.helper

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class VibrationHelperMh(private val mhContext: Context) {

  fun mhVibrate() {
    val vibrator = mhContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
      vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
      vibrator.vibrate(200)
    }
  }

}
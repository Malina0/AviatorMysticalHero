package com.aviatormysticalhero.helper

import android.content.Context
import com.aviatormysticalhero.R

class StorageHelperMh(contextMh: Context) {

  companion object {
    const val STORAGE_MH = "storageMh"
    const val HIGH_MH = "highMh"
    const val BG_MH = "bgMh"
    const val VIBRATION_MH = "vibrationMh"
  }

  private val storageMh = contextMh.getSharedPreferences(STORAGE_MH, Context.MODE_PRIVATE)

  fun mhHighScoreGet() = storageMh.getInt(HIGH_MH, 0)

  fun mhBgGet() = storageMh.getInt(BG_MH, R.drawable.mh_bg_1)

  fun mhVibrationGet() = storageMh.getBoolean(VIBRATION_MH, true)

  fun mhHightScoreSet(hgValue: Int) = storageMh.edit().putInt(HIGH_MH, hgValue).apply()

  fun mhBgSet(hgValue: Int) = storageMh.edit().putInt(BG_MH, hgValue).apply()

  fun mhVibrationSet(hgValue: Boolean) = storageMh.edit().putBoolean(VIBRATION_MH, hgValue).apply()

}
package com.aviatormysticalhero.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aviatormysticalhero.R
import com.aviatormysticalhero.databinding.FragmentGameMhBinding
import com.aviatormysticalhero.helper.StorageHelperMh
import com.aviatormysticalhero.helper.VibrationHelperMh
import com.aviatormysticalhero.view.PrimaryView

class GameMhFragment : Fragment() {
  private val bindingMh by lazy { FragmentGameMhBinding.inflate(layoutInflater) }
  private val storageHelperMh by lazy { StorageHelperMh(requireActivity()) }
  private val vibrationHelperMh by lazy { VibrationHelperMh(requireActivity()) }
  private val mhField by lazy {
    PrimaryView(
      requireContext(), mhEnd = { mhScore ->
        requireActivity().intent.putExtra("mhScore", mhScore)
        Handler(Looper.getMainLooper()).postDelayed({
          if (isAdded) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.resultMhFragment)
          }
        }, 1000)
      }, mhMoved = { mhScore ->
        if (storageHelperMh.mhVibrationGet()) {
          vibrationHelperMh.mhVibrate()
        }
        bindingMh.mhScore.text = "Score: ${mhScore}"
        if (storageHelperMh.mhHighScoreGet() < mhScore) {
          bindingMh.mhHigh.text = "High Score: ${mhScore}"
          storageHelperMh.mhHightScoreSet(mhScore.toInt())
        }
      }
    )
  }
  private var mhTime = 3600
  private var mhTimer: CountDownTimer? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = bindingMh.root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mhBg()
    mhScore()
    mhField()
    mhTimerStart()
    mhListeners()
  }

  private fun mhTimerStart() {
    if (mhTimer == null) {
      mhTimer = object : CountDownTimer(3600000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
          mhTime -= 1
          bindingMh.mhProgress.progress = mhTime.toFloat()
          bindingMh.mhTime.text = "%02d:%02d".format(mhTime / 60, mhTime % 60)
        }

        override fun onFinish() {
          if (isAdded) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.resultMhFragment)
          }
        }
      }
      mhTimer?.start()
    }
  }

  private fun mhScore() {
    val mhHigh = storageHelperMh.mhHighScoreGet()
    bindingMh.mhHigh.text = "High Score: ${mhHigh}"
  }

  private fun mhField() {
    if (bindingMh.mhField.childCount == 0) {
      bindingMh.mhField.addView(mhField.apply {
        layoutParams = FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.MATCH_PARENT
        )
      })
    }
  }

  private fun mhBg() {
    val mhBg = storageHelperMh.mhBgGet()
    bindingMh.mhBg.setImageResource(mhBg)
  }

  private fun mhListeners() {
    bindingMh.mhSettings.setOnClickListener {
      findNavController().navigate(R.id.settingsMhFragment)
    }
    bindingMh.mhReset.setOnClickListener {
      mhField.mGame.newGame()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    mhTimer?.cancel()
    mhTimer = null
  }

}
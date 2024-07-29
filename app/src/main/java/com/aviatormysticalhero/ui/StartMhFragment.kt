package com.aviatormysticalhero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aviatormysticalhero.R
import com.aviatormysticalhero.databinding.FragmentStartMhBinding
import com.aviatormysticalhero.helper.StorageHelperMh

class StartMhFragment : Fragment() {
  private val bindingMh by lazy { FragmentStartMhBinding.inflate(layoutInflater) }
  private val storageHelperMh by lazy { StorageHelperMh(requireActivity()) }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = bindingMh.root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mhBg()
    mhScore()
    mhListeners()
  }

  private fun mhBg() {
    val mhBg = storageHelperMh.mhBgGet()
    bindingMh.mhBg.setImageResource(mhBg)
  }

  private fun mhScore() {
    val mhHigh = storageHelperMh.mhHighScoreGet()
    bindingMh.mhHigh.text = "High Score: ${mhHigh}"
  }

  private fun mhListeners() {
    bindingMh.mhSettings.setOnClickListener {
      findNavController().navigate(R.id.settingsMhFragment)
    }
    bindingMh.mhStart.setOnClickListener {
      findNavController().navigate(R.id.gameMhFragment)
    }
  }
}
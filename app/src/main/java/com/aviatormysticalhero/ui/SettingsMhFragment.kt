package com.aviatormysticalhero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aviatormysticalhero.R
import com.aviatormysticalhero.databinding.FragmentSettingsMhBinding
import com.aviatormysticalhero.helper.StorageHelperMh

class SettingsMhFragment : Fragment() {
  private val bindingMh by lazy { FragmentSettingsMhBinding.inflate(layoutInflater) }
  private val storageHelperMh by lazy { StorageHelperMh(requireActivity()) }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = bindingMh.root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mhBg()
    mhVibration()
    mhListeners()
  }

  private fun mhVibration() {
    val mhVibration = storageHelperMh.mhVibrationGet()
    bindingMh.mhVibration.isChecked = mhVibration
  }

  private fun mhBg() {
    val mhBg = storageHelperMh.mhBgGet()
    bindingMh.mhBg.setImageResource(mhBg)
    mhBgPreview()
  }

  private fun mhBgPreview() {
    bindingMh.mhCb1.visibility = View.VISIBLE
    bindingMh.mhCb2.visibility = View.VISIBLE
    bindingMh.mhCb3.visibility = View.VISIBLE
    val mhBg = storageHelperMh.mhBgGet()
    when (mhBg) {
      R.drawable.mh_bg_1 -> bindingMh.mhCb1.visibility = View.GONE
      R.drawable.mh_bg_2 -> bindingMh.mhCb2.visibility = View.GONE
      R.drawable.mh_bg_3 -> bindingMh.mhCb3.visibility = View.GONE
    }
  }

  private fun mhListeners() {
    bindingMh.mhBack.setOnClickListener {
      findNavController().navigateUp()
    }
    bindingMh.mhVibration.setOnCheckedChangeListener { _, mhVibration ->
      storageHelperMh.mhVibrationSet(mhVibration)
    }
    bindingMh.mhBg1.setOnClickListener {
      storageHelperMh.mhBgSet(R.drawable.mh_bg_1)
      mhBg()
      mhBgPreview()
    }
    bindingMh.mhBg2.setOnClickListener {
      storageHelperMh.mhBgSet(R.drawable.mh_bg_2)
      mhBg()
      mhBgPreview()
    }
    bindingMh.mhBg3.setOnClickListener {
      storageHelperMh.mhBgSet(R.drawable.mh_bg_3)
      mhBg()
      mhBgPreview()
    }
  }
}
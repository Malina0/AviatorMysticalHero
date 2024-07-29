package com.aviatormysticalhero.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aviatormysticalhero.R
import com.aviatormysticalhero.databinding.FragmentSplashMhBinding
import com.aviatormysticalhero.helper.StorageHelperMh

class SplashMhFragment : Fragment() {
  private val bindingMh by lazy { FragmentSplashMhBinding.inflate(layoutInflater) }
  private val storageHelperMh by lazy { StorageHelperMh(requireActivity()) }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = bindingMh.root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val mhBg = storageHelperMh.mhBgGet()
    bindingMh.mhBg.setImageResource(mhBg)
    Handler(Looper.getMainLooper()).postDelayed({
      if (isAdded) {
        findNavController().popBackStack()
        findNavController().navigate(R.id.startMhFragment)
      }
    }, 1000)
  }
}
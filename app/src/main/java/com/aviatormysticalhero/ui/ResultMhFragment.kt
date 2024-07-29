package com.aviatormysticalhero.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aviatormysticalhero.databinding.FragmentResultMhBinding
import com.aviatormysticalhero.helper.StorageHelperMh

class ResultMhFragment : Fragment() {
  private val bindingMh by lazy { FragmentResultMhBinding.inflate(layoutInflater) }
  private val storageHelperMh by lazy { StorageHelperMh(requireActivity()) }
  private val mhScore by lazy {
    requireActivity().intent.getIntExtra("mhScore", 0)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ) = bindingMh.root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mhBg()
    mhResult()
    mhListeners()
  }

  private fun mhBg() {
    val mhBg = storageHelperMh.mhBgGet()
    bindingMh.mhBg.setImageResource(mhBg)
  }

  private fun mhResult() {
    if (mhScore >= 2048) {
      bindingMh.mhWin.visibility = View.VISIBLE
      bindingMh.mhLose.visibility = View.GONE
    } else {
      bindingMh.mhLose.visibility = View.VISIBLE
      bindingMh.mhWin.visibility = View.GONE
    }
  }

  private fun mhListeners() {
    bindingMh.mhContinue.setOnClickListener {
      findNavController().navigateUp()
    }
  }
}
package com.example.flo.ui.main.locker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.flo.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetLayout : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLayoutBinding.inflate(inflater, container,false)

        binding.bottomSheetDeleteIv.setOnClickListener {
            Toast.makeText(requireContext(),"눌림", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}
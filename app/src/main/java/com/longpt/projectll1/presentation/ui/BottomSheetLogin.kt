package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.longpt.projectll1.databinding.ActivityLoginBinding

class BottomSheetLogin : BottomSheetDialogFragment() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = ActivityLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvRegister.setOnClickListener {
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            intent.putExtra("from", "bottomsheet")
            startActivity(intent)
        }
    }
}
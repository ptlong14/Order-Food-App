package com.longpt.projectll1.presentation.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.databinding.FragmentAccountBinding
import com.longpt.projectll1.utils.showToast


class UserFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val currentUser get() = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentUser != null) {
            binding.btnLogout.setOnClickListener {
                AlertDialog.Builder(requireContext()).setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất") { _, _ ->
                        FirebaseAuth.getInstance().signOut()
                    }.setNegativeButton("Hủy", null).setCancelable(false).show()
            }
        } else {
            //show bottomSheet signIn
        }
        binding.btnChat.setOnClickListener {
            "Not yet implemented".showToast(requireContext())
        }
        binding.btnAccountProfile.setOnClickListener {
            val intent = Intent(requireContext(), UserInformationActivity::class.java)
            startActivity(intent)
        }
        binding.btnPrivacyPolicy.setOnClickListener {
            val intent= Intent(requireContext(), PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
        binding.btnShippingInfor.setOnClickListener {
            val intent= Intent(requireContext(), ShippingInforActivity::class.java)
            startActivity(intent)
        }
    }
}
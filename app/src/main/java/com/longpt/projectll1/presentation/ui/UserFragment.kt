package com.longpt.projectll1.presentation.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.data.sharedPref.UserStorage
import com.longpt.projectll1.databinding.FragmentAccountBinding
import com.longpt.projectll1.utils.AlertUtils
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
                        UserStorage.clearUser(requireContext())
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                    }.setNegativeButton("Hủy", null).setCancelable(false).show()
            }
        } else {
            binding.btnLogout.text = "Đăng nhập"
            binding.btnLogout.setOnClickListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }
        binding.btnChat.setOnClickListener {
            "Tính năng này đang được phát triển".showToast(requireContext())
        }
        binding.btnAccountProfile.setOnClickListener {
            if (currentUser == null) {
                AlertUtils.showLoginAlert(requireContext()) {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.putExtra("from", "client")
                    startActivity(intent)
                }
                return@setOnClickListener
            }
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
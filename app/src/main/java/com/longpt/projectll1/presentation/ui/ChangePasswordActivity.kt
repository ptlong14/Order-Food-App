package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirebaseAuthDataSource
import com.longpt.projectll1.data.repositoryImpl.AuthRepositoryImpl
import com.longpt.projectll1.databinding.ActivityChangePasswordBinding
import com.longpt.projectll1.domain.usecase.ChangePasswordUC
import com.longpt.projectll1.domain.usecase.LoginUC
import com.longpt.projectll1.domain.usecase.RegisterUC
import com.longpt.projectll1.domain.usecase.ResetPasswordUC
import com.longpt.projectll1.presentation.factory.AuthViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AuthViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var authViewModel: AuthViewModel
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val repoAuth = AuthRepositoryImpl(FirebaseAuthDataSource())
        val loginUC = LoginUC(repoAuth)
        val registerUC = RegisterUC(repoAuth)
        val changePasswordUC = ChangePasswordUC(repoAuth)
        val resetPasswordUC = ResetPasswordUC(repoAuth)
        val authFactory = AuthViewModelFactory(
            loginUC,
            registerUC,
            changePasswordUC,
            resetPasswordUC
        )
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        binding.btnSubmit.setOnClickListener {
            val email = currentUser!!.email
            val oldPassword = binding.etOldPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                "Nhập đầy đủ thông tin".showToast(this)
                return@setOnClickListener
            }
            if (newPassword.length < 6) {
                "Mật khẩu phải có ít nhất 6 ký tự".showToast(this)
                return@setOnClickListener
            }
            if (newPassword != confirmPassword) {
                "Mật khẩu không khớp".showToast(this)
                return@setOnClickListener
            }
            authViewModel.changePassword(email!!, oldPassword, newPassword)
        }
        binding.iBtnBack.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            authViewModel.changePasswordResult.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> {
                        res.exception.message?.showToast(this@ChangePasswordActivity)
                    }

                    is TaskResult.Success -> {
                        "Đổi mật khẩu thành công".showToast(this@ChangePasswordActivity)
                        finish()
                    }
                }
            }
        }
    }
}
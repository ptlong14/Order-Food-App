package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirebaseAuthDataSource
import com.longpt.projectll1.data.repositoryImpl.AuthRepositoryImpl
import com.longpt.projectll1.databinding.ActivityForgotPasswordBinding
import com.longpt.projectll1.domain.usecase.ChangePasswordUC
import com.longpt.projectll1.domain.usecase.LoginUC
import com.longpt.projectll1.domain.usecase.RegisterUC
import com.longpt.projectll1.domain.usecase.ResetPasswordUC
import com.longpt.projectll1.presentation.factory.AuthViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AuthViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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
            val email = binding.etEmail.text.toString()
            authViewModel.resetPassword(email)
        }
        lifecycleScope.launch {
            authViewModel.resetPasswordResult.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> {
                        res.exception.message?.showToast(this@ForgotPasswordActivity)
                        return@collect
                    }

                    is TaskResult.Success -> {
                        "Vui lòng kiểm tra email để đặt lại mật khẩu".showToast(this@ForgotPasswordActivity)
                        startCountdown(binding.btnSubmit)
                    }
                }
            }
        }
        binding.iBtnBack.setOnClickListener {
            finish()
        }
    }

    private fun startCountdown(button: Button) {
        button.isEnabled = false

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                button.text = "$seconds s"
            }

            override fun onFinish() {
                button.text = "Gửi lại"
                button.isEnabled = true
            }
        }.start()
    }
}
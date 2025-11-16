package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirebaseAuthDataSource
import com.longpt.projectll1.data.repositoryImpl.AuthRepositoryImpl
import com.longpt.projectll1.databinding.ActivityRegisterBinding
import com.longpt.projectll1.domain.usecase.ChangePasswordUC
import com.longpt.projectll1.domain.usecase.LoginUC
import com.longpt.projectll1.domain.usecase.RegisterUC
import com.longpt.projectll1.domain.usecase.ResetPasswordUC
import com.longpt.projectll1.presentation.factory.AuthViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AuthViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var from: String
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        from = intent.getStringExtra("from") ?: ""

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                "Nhập đầy đủ thông tin".showToast(this)
                return@setOnClickListener
            }
            //validate email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                "Email không hợp lệ".showToast(this)
                return@setOnClickListener
            }
            //validate password
            if (password.length < 6) {
                "Mật khẩu phải có ít nhất 6 ký tự".showToast(this)
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                "Mật khẩu không khớp".showToast(this)
                return@setOnClickListener
            }

            authViewModel.register(email, password)
        }
        binding.tvLogin.setOnClickListener {
            if (from == "login") startActivity(Intent(this, LoginActivity::class.java))
            else finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.registerResult.collect { res ->
                    when (res) {
                        TaskResult.Loading -> {}
                        is TaskResult.Error -> {
                            res.exception.message?.showToast(this@RegisterActivity)
                            return@collect
                        }

                        is TaskResult.Success -> {
                            "Đăng ký thành công. Vui lòng đăng nhập lại".showToast(this@RegisterActivity)
                            if (from == "bottomsheet") finish()
                            else startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
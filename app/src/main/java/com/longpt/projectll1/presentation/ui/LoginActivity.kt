package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.longpt.projectll1.data.sharedPref.UserStorage
import com.longpt.projectll1.databinding.ActivityLoginBinding
import com.longpt.projectll1.domain.usecase.ChangePasswordUC
import com.longpt.projectll1.domain.usecase.LoginUC
import com.longpt.projectll1.domain.usecase.RegisterUC
import com.longpt.projectll1.domain.usecase.ResetPasswordUC
import com.longpt.projectll1.presentation.factory.AuthViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AuthViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    lateinit var binding: ActivityLoginBinding
    lateinit var from: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        from = intent.getStringExtra("from").toString()
        val repoAuth = AuthRepositoryImpl(FirebaseAuthDataSource())
        val loginUC = LoginUC(repoAuth)
        val registerUC = RegisterUC(repoAuth)
        val changePasswordUC = ChangePasswordUC(repoAuth)
        val resetPasswordUC = ResetPasswordUC(repoAuth)
        val authFactory = AuthViewModelFactory(
            loginUC, registerUC, changePasswordUC, resetPasswordUC
        )
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        if (from == "client") {
            binding.iBtnBack.visibility = View.VISIBLE
            binding.iBtnBack.setOnClickListener {
                finish()
            }
        } else {
            binding.iBtnBack.visibility = View.GONE
        }

        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                "Nhập đầy đủ thông tin".showToast(this)
                return@setOnClickListener
            }

            authViewModel.login(email, password)
        }
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginResult.collect { res ->
                    when (res) {
                        TaskResult.Loading -> {}
                        is TaskResult.Error -> {
                            res.exception.message?.showToast(this@LoginActivity)
                            return@collect
                        }

                        is TaskResult.Success -> {
                            val user = res.data
                            "Đăng nhập thành công. Chào mừng ${user.name}".showToast(this@LoginActivity)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            UserStorage.saveUser(this@LoginActivity, user.name, user.avatarUrl)
                            finish()
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
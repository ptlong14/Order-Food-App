package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
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
import com.longpt.projectll1.databinding.ActivityLoginBinding
import com.longpt.projectll1.domain.usecase.LoginUC
import com.longpt.projectll1.domain.usecase.RegisterUC
import com.longpt.projectll1.presentation.factory.AuthViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AuthViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repoAuth = AuthRepositoryImpl(FirebaseAuthDataSource())
        val loginUC = LoginUC(repoAuth)
        val registerUC = RegisterUC(repoAuth)
        val authFactory = AuthViewModelFactory(
            loginUC, registerUC
        )
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                "Nhập đầy đủ thông tin".showToast(this)
                return@setOnClickListener
            }

            authViewModel.login(email, password)

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    authViewModel.loginResult.collect { res ->
                        when (res) {
                            TaskResult.Loading -> {}
                            is TaskResult.Error -> {
                                res.exception.message?.showToast(this@LoginActivity)
                            }

                            is TaskResult.Success -> {
                                val user = res.data
                                "Đăng nhập thành công. Chào mừng ${user.name}".showToast(this@LoginActivity)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("from", "login")
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
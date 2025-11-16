package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.ChangePasswordUC
import com.longpt.projectll1.domain.usecase.LoginUC
import com.longpt.projectll1.domain.usecase.RegisterUC
import com.longpt.projectll1.domain.usecase.ResetPasswordUC
import com.longpt.projectll1.presentation.viewModel.AuthViewModel

class AuthViewModelFactory(
    private val loginUC: LoginUC,
    private val registerUC: RegisterUC,
    private val changePasswordUC: ChangePasswordUC,
    private val resetPasswordUC: ResetPasswordUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(loginUC, registerUC, resetPasswordUC, changePasswordUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
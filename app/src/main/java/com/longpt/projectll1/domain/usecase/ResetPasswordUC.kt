package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.AuthRepository

class ResetPasswordUC(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String) = authRepository.sendResetPassword(email)
}
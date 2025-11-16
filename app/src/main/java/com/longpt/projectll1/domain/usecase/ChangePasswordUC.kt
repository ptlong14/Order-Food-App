package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.AuthRepository

class ChangePasswordUC(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, oldPassword: String, newPassword: String) =
        authRepository.changePassword(email, oldPassword, newPassword)
}
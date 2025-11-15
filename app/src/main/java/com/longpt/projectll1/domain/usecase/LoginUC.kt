package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.AuthRepository

class LoginUC(val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.login(email, password)
}
package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.AuthRepository

class RegisterUC(val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.register(email, password)
}
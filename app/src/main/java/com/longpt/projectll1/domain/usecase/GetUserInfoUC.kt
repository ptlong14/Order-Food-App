package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.UserRepository

class GetUserInfoUC(private val repository: UserRepository) {
    operator fun invoke(userId: String)= repository.getUserById(userId)
}
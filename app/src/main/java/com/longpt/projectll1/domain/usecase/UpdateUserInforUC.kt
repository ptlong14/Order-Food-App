package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.UserRepository

class UpdateUserInforUC(private val repository: UserRepository) {
    suspend operator fun invoke(userId: String, field: String, value: String)= repository.updateUserInfor(userId, field, value)
}
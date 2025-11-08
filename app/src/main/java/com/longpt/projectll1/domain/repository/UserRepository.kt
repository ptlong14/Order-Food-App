package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.User
import java.io.File

interface UserRepository {
    suspend fun getUserById(userId: String): TaskResult<User>
    suspend fun updateUserInfor(field: String, value: Any): TaskResult<Unit>
    suspend fun updateAvatar(userId: String, fileImg: File): TaskResult<Unit>
}
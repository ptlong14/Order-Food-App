package com.longpt.projectll1.domain.repository

import android.net.Uri
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserById(userId: String): Flow<TaskResult<User>>
    suspend fun updateUserInfor(userId: String, field: String, value: String): TaskResult<Unit>
    fun updateAvatar(userId: String, uri: Uri): Flow<TaskResult<Unit>>
}
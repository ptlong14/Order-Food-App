package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): TaskResult<User>

    suspend fun register(email: String, password: String): TaskResult<Unit>
}

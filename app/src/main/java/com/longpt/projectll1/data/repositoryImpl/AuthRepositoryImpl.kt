package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.UserMapper
import com.longpt.projectll1.data.remote.FirebaseAuthDataSource
import com.longpt.projectll1.domain.model.User
import com.longpt.projectll1.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authDataSource: FirebaseAuthDataSource
) : AuthRepository {
    override suspend fun login(email: String, password: String): TaskResult<User> {
        return when (val result = authDataSource.login(email, password)) {
            is TaskResult.Success -> {
                val mapped = UserMapper.fromDtoToDomain(result.data)
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> {
                TaskResult.Error(result.exception)
            }

            else -> TaskResult.Loading
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): TaskResult<Unit> {
        return authDataSource.register(email, password)
    }
}
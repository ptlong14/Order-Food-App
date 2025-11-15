package com.longpt.projectll1.data.repositoryImpl

import android.net.Uri
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.UserMapper
import com.longpt.projectll1.data.remote.CloudinaryService
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.User
import com.longpt.projectll1.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    val dataSource: FirestoreDataSource,
    val cloudinaryService: CloudinaryService
): UserRepository {
    override fun getUserById(userId: String): Flow<TaskResult<User>> {
        return dataSource.getUserInfo(userId).map { result ->
            when(result){
                is TaskResult.Loading -> TaskResult.Loading
                is TaskResult.Error -> TaskResult.Error(result.exception)
                is TaskResult.Success -> {
                    val mapped= UserMapper.fromDtoToDomain(result.data)
                    TaskResult.Success(mapped)
                }
            }
        }
    }

    override suspend fun updateUserInfor(
        userId: String,
        field: String,
        value: String
    ): TaskResult<Unit> {
        return dataSource.updateUserInfor(userId, field, value)
    }

    override fun updateAvatar(
        userId: String,
        uri: Uri
    ): Flow<TaskResult<Unit>> {
        return cloudinaryService.upload(uri).map { result->
            when(result){
                is TaskResult.Loading -> TaskResult.Loading
                is TaskResult.Error -> TaskResult.Error(result.exception)

                is TaskResult.Success -> {
                    val res = dataSource.updateAvatar(userId, result.data)

                    if (res is TaskResult.Error) {
                        TaskResult.Error(res.exception)
                    } else {
                        TaskResult.Success(Unit)
                    }
                }
            }
        }
    }
}
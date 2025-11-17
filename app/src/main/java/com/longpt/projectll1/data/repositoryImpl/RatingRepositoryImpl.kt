package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.RatingMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.repository.RatingRepository

class RatingRepositoryImpl(private val dataSource: FirestoreDataSource) : RatingRepository {
    override suspend fun getRatingList(foodId: String): TaskResult<List<Rating>> {
        return when (val res = dataSource.getRatingList(foodId)) {
            is TaskResult.Success -> {
                val mapped = res.data.map {
                    RatingMapper.fromDtoToDomain(it)
                }
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }

    override suspend fun addUpRating(
        rating: Rating, foodId: String, userId: String
    ): TaskResult<Unit> {
        val dto = RatingMapper.fromDomainToDto(rating)
        return dataSource.addUpRating(dto, foodId, userId)
    }

    override suspend fun getRatingByUserId(
        userId: String,
        foodId: String
    ): TaskResult<Rating> {
        return when (val res = dataSource.getRatingByUserId(userId, foodId)) {
            is TaskResult.Success -> {
                val mapped = RatingMapper.fromDtoToDomain(res.data)
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }
}
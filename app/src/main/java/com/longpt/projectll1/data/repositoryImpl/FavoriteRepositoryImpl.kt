package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.FoodMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(private val dataSource: FirestoreDataSource) : FavoriteRepository {
    override suspend fun addToFavorite(
        food: Food, userId: String
    ): TaskResult<Unit> {
        val dto = FoodMapper.fromDomainToDto(food)
        return dataSource.addToFavorite(dto, userId)
    }

    override suspend fun removeFavorite(foodId: String, userId: String): TaskResult<Unit> {
        return dataSource.removeFromFavorite(foodId, userId)
    }

    override fun getFavoriteList(userId: String): Flow<TaskResult<List<Food>>> {
        return dataSource.getFavoriteList(userId).map { result ->
            when (result) {
                is TaskResult.Loading -> TaskResult.Loading
                is TaskResult.Error -> TaskResult.Error(result.exception)
                is TaskResult.Success -> {
                    val mapped = result.data.map { dto -> FoodMapper.fromDtoToDomain(dto) }
                    TaskResult.Success(mapped)
                }
            }
        }
    }
}
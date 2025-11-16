package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.FoodMapper
import com.longpt.projectll1.data.mapper.RatingMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodRepositoryImpl(private val dataSource: FirestoreDataSource) : FoodRepository {
    override suspend fun getFoodListByCategory(category: String): TaskResult<List<Food>> {
        return when (val res = dataSource.getFoodListByCategory(category)) {
            is TaskResult.Success -> {
                val mapped = res.data.map { dto -> FoodMapper.fromDtoToDomain(dto) }
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }

    override suspend fun getFoodById(id: String): TaskResult<Food> {
        return when (val res = dataSource.getFoodById(id)) {
            is TaskResult.Success -> {
                val mapped = FoodMapper.fromDtoToDomain(res.data)
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }

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

    override suspend fun addRating(
        rating: Rating,
        foodId: String,
        userId: String
    ): TaskResult<Unit> {
        val dto = RatingMapper.fromDomainToDto(rating)
        return dataSource.createRating(dto, foodId, userId)
    }

    override suspend fun updateRating(
        rating: Rating,
        foodId: String,
        userId: String
    ): TaskResult<Unit> {
        TODO("Not yet implemented")
    }


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

    override suspend fun getBestSellerFoodList(): TaskResult<List<Food>> {
       return when (val result = dataSource.getBestSellerFoodList()) {
            is TaskResult.Success -> {
                val mapped = result.data.map { dto -> FoodMapper.fromDtoToDomain(dto) }
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> TaskResult.Error(result.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }

    override suspend fun getNewFoodList(): TaskResult<List<Food>>{
        return when (val res = dataSource.get8NewFoodList()) {
            is TaskResult.Success -> {
                val mapped = res.data.map { dto -> FoodMapper.fromDtoToDomain(dto) }
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }

    override suspend fun getTopRatedFoodList(): TaskResult<List<Food>> {
        return when (val res = dataSource.getTopRatedFoodList()) {
            is TaskResult.Success -> {
                val mapped = res.data.map { dto -> FoodMapper.fromDtoToDomain(dto) }
                TaskResult.Success(mapped)
            }

            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }
}
package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.FoodMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.repository.FoodRepository

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
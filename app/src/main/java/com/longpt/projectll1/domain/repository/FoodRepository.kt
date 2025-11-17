package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food

interface FoodRepository {
    suspend fun getFoodListByCategory(category: String): TaskResult<List<Food>>
    suspend fun getFoodById(id: String): TaskResult<Food>
    suspend fun getBestSellerFoodList(): TaskResult<List<Food>>
    suspend fun getNewFoodList(): TaskResult<List<Food>>
    suspend fun getTopRatedFoodList(): TaskResult<List<Food>>
}

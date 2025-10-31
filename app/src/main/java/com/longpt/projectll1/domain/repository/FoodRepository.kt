package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun getFoodListByCategory(category: String): TaskResult<List<Food>>
    suspend fun getFoodById(id: String): TaskResult<Food>
    suspend fun addToFavorite(food: Food, userId: String): TaskResult<Unit>
    fun isFavorite(foodId: String, userId: String): Flow<TaskResult<Boolean>>
    suspend fun removeFavorite(foodId: String, userId: String): TaskResult<Unit>
    fun getFavoriteList(userId: String): Flow<TaskResult<List<Food>>>

    suspend fun getBestSellerFoodList(): TaskResult<List<Food>>

    suspend fun getNewFoodList(): TaskResult<List<Food>>
    //Dựa trên lượt xem / lượt click / lượt thêm vào giỏ trong khoảng thời gian gần nhất, orderBy viewCount DESC
    fun getTrendingFoodList(): Flow<TaskResult<List<Food>>>
    suspend fun getTopRatedFoodList(): TaskResult<List<Food>>
}

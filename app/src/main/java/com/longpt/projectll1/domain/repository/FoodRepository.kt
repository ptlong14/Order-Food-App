package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.model.Rating
import kotlinx.coroutines.flow.Flow
interface FoodRepository {
    suspend fun getFoodListByCategory(category: String): TaskResult<List<Food>>
    suspend fun getFoodById(id: String): TaskResult<Food>
    suspend fun getRatingList(foodId: String): TaskResult<List<Rating>>
    suspend fun addRating(rating: Rating, foodId: String, userId: String): TaskResult<Unit>
    suspend fun updateRating(rating: Rating, foodId: String, userId: String): TaskResult<Unit>
    suspend fun addToFavorite(food: Food, userId: String): TaskResult<Unit>
    suspend fun removeFavorite(foodId: String, userId: String): TaskResult<Unit>
    fun getFavoriteList(userId: String): Flow<TaskResult<List<Food>>>
    suspend fun getBestSellerFoodList(): TaskResult<List<Food>>
    suspend fun getNewFoodList(): TaskResult<List<Food>>
    suspend fun getTopRatedFoodList(): TaskResult<List<Food>>
}

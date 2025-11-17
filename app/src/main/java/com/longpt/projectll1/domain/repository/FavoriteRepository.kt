package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addToFavorite(food: Food, userId: String): TaskResult<Unit>
    suspend fun removeFavorite(foodId: String, userId: String): TaskResult<Unit>
    fun getFavoriteList(userId: String): Flow<TaskResult<List<Food>>>
}
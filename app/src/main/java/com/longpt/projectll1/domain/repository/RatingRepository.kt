package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Rating

interface RatingRepository {
    suspend fun getRatingList(foodId: String): TaskResult<List<Rating>>
    suspend fun addUpRating(rating: Rating, foodId: String, userId: String): TaskResult<Unit>
    suspend fun getRatingByUserId(userId: String, foodId: String): TaskResult<Rating>
}
package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.repository.FoodRepository

class RemoveFromFavoriteUC(private val foodRepository: FoodRepository)  {
    suspend operator fun invoke(foodId: String, userId: String): TaskResult<Unit> {
       return foodRepository.removeFavorite(foodId, userId)
    }
}
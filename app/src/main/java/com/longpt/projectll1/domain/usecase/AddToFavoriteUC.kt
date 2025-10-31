package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.repository.FoodRepository


class AddToFavoriteUC(val foodRepository: FoodRepository) {
    suspend operator fun invoke(food: Food, userId: String): TaskResult<Unit> {
        return foodRepository.addToFavorite(food, userId)
    }
}
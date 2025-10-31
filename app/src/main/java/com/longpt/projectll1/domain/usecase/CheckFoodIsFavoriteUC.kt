package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FoodRepository

class CheckFoodIsFavoriteUC(private val foodRepository: FoodRepository)  {
    operator fun invoke(foodId: String, userId: String) = foodRepository.isFavorite(foodId, userId)
}
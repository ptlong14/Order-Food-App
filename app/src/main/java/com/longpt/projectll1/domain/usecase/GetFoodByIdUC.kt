package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FoodRepository

class GetFoodByIdUC(private val foodRepository: FoodRepository) {
    suspend operator fun invoke(foodId: String) = foodRepository.getFoodById(foodId)
}
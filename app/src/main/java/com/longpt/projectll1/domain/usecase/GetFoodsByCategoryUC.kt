package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FoodRepository

class GetFoodsByCategoryUC(private val foodRepository: FoodRepository) {
    suspend operator fun invoke(category: String) = foodRepository.getFoodListByCategory(category)
}
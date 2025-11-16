package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FoodRepository

class GetRatingListByFoodIdUC(private val foodRepository: FoodRepository) {
    suspend operator fun invoke(foodId: String) = foodRepository.getRatingList(foodId)
}

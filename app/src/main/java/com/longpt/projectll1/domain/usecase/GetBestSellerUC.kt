package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FoodRepository

class GetBestSellerUC(private val foodRepository: FoodRepository) {
    suspend operator fun invoke() = foodRepository.getBestSellerFoodList()
}

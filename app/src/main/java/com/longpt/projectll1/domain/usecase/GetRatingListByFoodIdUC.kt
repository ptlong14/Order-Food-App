package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.RatingRepository

class GetRatingListByFoodIdUC(private val ratingRepository: RatingRepository) {
    suspend operator fun invoke(foodId: String) = ratingRepository.getRatingList(foodId)
}

package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.RatingRepository

class GetRatingByUserIdUC(val repository: RatingRepository) {
    suspend operator fun invoke(userId: String, foodId: String) =
        repository.getRatingByUserId(userId, foodId)
}
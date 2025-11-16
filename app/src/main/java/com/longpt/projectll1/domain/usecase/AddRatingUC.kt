package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.repository.FoodRepository

class AddRatingUC(val repository: FoodRepository) {
    suspend operator fun invoke(rating: Rating, foodId: String, userId: String) =
        repository.addRating(rating, foodId, userId)
}

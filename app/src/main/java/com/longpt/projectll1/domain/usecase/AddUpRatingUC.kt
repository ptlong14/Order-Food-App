package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.repository.RatingRepository

class AddUpRatingUC(val repository: RatingRepository) {
    suspend operator fun invoke(rating: Rating, foodId: String, userId: String) =
        repository.addUpRating(rating, foodId, userId)
}

package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.repository.FavoriteRepository

class RemoveItemFromFavoriteUC(private val repository: FavoriteRepository) {
    suspend operator fun invoke(foodId: String, userId: String): TaskResult<Unit> {
        return repository.removeFavorite(foodId, userId)
    }
}
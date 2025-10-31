package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FoodRepository

class GetFavFoodsUC(val repo: FoodRepository) {
    operator fun invoke(userId: String) = repo.getFavoriteList(userId)
}

package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FavoriteRepository

class GetFavFoodsUC(val repo: FavoriteRepository) {
    operator fun invoke(userId: String) = repo.getFavoriteList(userId)
}

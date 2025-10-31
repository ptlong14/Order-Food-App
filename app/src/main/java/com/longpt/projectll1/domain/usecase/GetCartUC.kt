package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.CartRepository

class GetCartUC(val repo: CartRepository) {
     operator fun invoke(userId: String) = repo.getCartList(userId)
}
package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.repository.CartRepository

class AddToCartUC(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartItem: CartItem, userId: String): TaskResult<Unit> {
        return cartRepository.addToCart(cartItem, userId)
    }
}
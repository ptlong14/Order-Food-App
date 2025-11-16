package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.repository.CartRepository

class RemoveItemFromCartUC(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartItemId: String, userId: String): TaskResult<Unit> {
        return cartRepository.removeFromCart(cartItemId, userId)
    }
}
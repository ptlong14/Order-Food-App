package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.repository.CartRepository

class UpdateCartItemQuantityUC(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartItemId: String, newQuantity: Int, userId: String): TaskResult<Unit> {
        return cartRepository.updateCartItemQuantity(cartItemId, newQuantity, userId)
    }
}
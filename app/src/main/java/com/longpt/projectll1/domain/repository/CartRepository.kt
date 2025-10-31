package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartList(userId: String): Flow<TaskResult<List<CartItem>>>
    suspend fun addToCart(cartItem: CartItem, userId: String): TaskResult<Unit>
    suspend fun removeFromCart(cartItemId: String, userId: String): TaskResult<Unit>
    suspend fun updateCartItemQuantity(cartItemId: String, newQuantity: Int, userId: String): TaskResult<Unit>
}
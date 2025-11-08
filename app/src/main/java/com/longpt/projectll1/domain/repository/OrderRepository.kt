package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.modelDTO.CartItemDto
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(order: Order): TaskResult<Unit>

    fun getUserOrdersByStatus(userId: String, status: String): Flow<TaskResult<List<Order>>>
    suspend fun getUserOrderDetail(orderId: String, userId: String): TaskResult<Order>
    suspend fun updateOrderStatus(orderId: String, newStatus: String): TaskResult<Unit>
    suspend fun cancelOrder(orderId: String, userId: String, reason: String): TaskResult<Unit>
    suspend fun ratingOrder(orderId: String, userId: String, rating: Int, comment: String): TaskResult<Unit>
    suspend fun reOrder(orderId: String, userId: String): TaskResult<List<CartItem>>
}
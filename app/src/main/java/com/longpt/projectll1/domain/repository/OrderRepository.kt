package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(order: Order): TaskResult<Unit>

    fun getUserOrdersByStatus(userId: String, status: String): Flow<TaskResult<List<Order>>>

    suspend fun getOrderById(orderId: String): TaskResult<Order>

    suspend fun updateOrderStatus(orderId: String, newStatus: String): TaskResult<Unit>
}
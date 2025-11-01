package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.OrderMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class OrderRepositoryImpl(private val dataSource: FirestoreDataSource): OrderRepository {
    override suspend fun createOrder(order: Order): TaskResult<Unit> {
       val dto = OrderMapper.fromDomainToDto(order)
        return dataSource.createOrder(dto)
    }

    override fun getUserOrders(userId: String): Flow<TaskResult<List<Order>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getOrderById(orderId: String): TaskResult<Order> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String
    ): TaskResult<Unit> {
        TODO("Not yet implemented")
    }

}

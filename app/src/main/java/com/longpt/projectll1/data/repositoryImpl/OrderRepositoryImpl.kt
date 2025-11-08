package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.CartMapper
import com.longpt.projectll1.data.mapper.OrderMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(private val dataSource: FirestoreDataSource): OrderRepository {
    override suspend fun createOrder(order: Order): TaskResult<Unit> {
       val dto = OrderMapper.fromDomainToDto(order)
        return dataSource.createOrder(dto)
    }

    override fun getUserOrdersByStatus(
        userId: String,
        status: String
    ): Flow<TaskResult<List<Order>>> {
        return dataSource.getUserOrdersByStatus(userId, status).map { res->
            when(res){
                is TaskResult.Loading-> TaskResult.Loading
                is TaskResult.Error -> TaskResult.Error(res.exception)
                is TaskResult.Success ->{
                    val mapped= res.data.map { dto-> OrderMapper.fromDtoToDomain(dto) }
                    TaskResult.Success(mapped)
                }
            }
        }
    }

    override suspend fun getUserOrderDetail(
        orderId: String,
        userId: String
    ): TaskResult<Order> {
       return when(val res= dataSource.getUserOrderDetail(orderId, userId)){
            is TaskResult.Loading-> TaskResult.Loading
            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Success ->{
                val mapped= OrderMapper.fromDtoToDomain(res.data)
                TaskResult.Success(mapped)
            }
       }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String
    ): TaskResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelOrder(
        orderId: String,
        userId: String,
        reason: String
    ): TaskResult<Unit> {
        return dataSource.canceledOrder(orderId, userId, reason)
    }

    override suspend fun ratingOrder(
        orderId: String,
        userId: String,
        rating: Int,
        comment: String
    ): TaskResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun reOrder(
        orderId: String,
        userId: String
    ): TaskResult<List<CartItem>> {
        return when(val res= dataSource.reOrder(orderId, userId)){
            is TaskResult.Loading-> TaskResult.Loading
            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Success ->{
                val mapped= CartMapper.toCartItems(res.data)
                TaskResult.Success(mapped)
            }
        }
    }

}

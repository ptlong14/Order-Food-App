package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.CartMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(private val dataSource: FirestoreDataSource) : CartRepository {
    override fun getCartList(userId: String): Flow<TaskResult<List<CartItem>>> {
        return dataSource.getCartList(userId).map { result ->
            when (result) {
                is TaskResult.Loading -> TaskResult.Loading
                is TaskResult.Error -> TaskResult.Error(result.exception)

                is TaskResult.Success -> {
                    val mapped = result.data.map { dto -> CartMapper.fromDtoToDomain(dto) }
                    TaskResult.Success(mapped)
                }
            }
        }
    }
    override suspend fun addToCart(
        cartItem: CartItem, userId: String
    ): TaskResult<Unit> {
        val cartItemDto = CartMapper.fromDomainToDto(cartItem)
        return dataSource.addToCart(cartItemDto, userId)
    }

    override suspend fun removeFromCart(
        cartItemId: String,
        userId: String
    ): TaskResult<Unit> {
        return dataSource.removeFromCart(cartItemId, userId)
    }

    override suspend fun updateCartItemQuantity(
        cartItemId: String,
        newQuantity: Int,
        userId: String
    ): TaskResult<Unit> {
       return dataSource.updateCartItemQuantity(cartItemId, newQuantity, userId)
    }
}
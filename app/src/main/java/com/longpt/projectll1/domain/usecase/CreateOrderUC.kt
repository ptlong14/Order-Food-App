package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.repository.OrderRepository

class CreateOrderUC(val orderRepository: OrderRepository) {
    suspend operator fun invoke(order: Order) = orderRepository.createOrder(order)
}
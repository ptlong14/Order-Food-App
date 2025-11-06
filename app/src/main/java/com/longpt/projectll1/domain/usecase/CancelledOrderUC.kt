package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.OrderRepository

class CancelledOrderUC(val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: String, userId: String, reason: String)= orderRepository.cancelOrder(orderId, userId, reason)
}

package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.OrderRepository

class GetUserOrderDetailUC(val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: String, userId: String) =
        orderRepository.getUserOrderDetail(orderId, userId)
}

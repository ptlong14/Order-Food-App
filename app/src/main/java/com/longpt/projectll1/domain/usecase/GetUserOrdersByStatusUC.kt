package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.OrderRepository

class GetUserOrdersByStatusUC(private val repo: OrderRepository) {
    operator fun invoke(userId:String, status:String)= repo.getUserOrdersByStatus(userId, status)
}
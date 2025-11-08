package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.CancelledOrderUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.GetUserOrderDetailUC
import com.longpt.projectll1.domain.usecase.GetUserOrdersByStatusUC
import com.longpt.projectll1.domain.usecase.ReOrderUC
import com.longpt.projectll1.presentation.viewModel.OrderViewModel

class OrderViewModelFactory(
    private val createOrderUC: CreateOrderUC,
    private val getUserOrdersByStatusUC: GetUserOrdersByStatusUC,
    private val getUserOrderDetailUC: GetUserOrderDetailUC,
    private val cancelledOrderUC: CancelledOrderUC,
    private val reOrderUC: ReOrderUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(createOrderUC, getUserOrdersByStatusUC, getUserOrderDetailUC, cancelledOrderUC, reOrderUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.presentation.viewModel.OrderViewModel

class OrderViewModelFactory(
    private val createOrderUC: CreateOrderUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(createOrderUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
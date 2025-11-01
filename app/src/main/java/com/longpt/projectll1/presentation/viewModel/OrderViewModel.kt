package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val createOrderUC: CreateOrderUC
): ViewModel() {
    private val _createOrderState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val createOrderState: StateFlow<TaskResult<Unit>> = _createOrderState


    fun createOrder(order: Order){
        viewModelScope.launch {
            _createOrderState.value = TaskResult.Loading
            val result = createOrderUC(order)
            _createOrderState.value = result
        }
    }

}
package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.usecase.CancelledOrderUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.GetUserOrderDetailUC
import com.longpt.projectll1.domain.usecase.GetUserOrdersByStatusUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val createOrderUC: CreateOrderUC,
    private val getUserOrdersByStatusUC: GetUserOrdersByStatusUC,
    private val getUserOrderDetailUC: GetUserOrderDetailUC,
    private val cancelledOrderUC: CancelledOrderUC
): ViewModel() {
    private val _createOrderState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val createOrderState: StateFlow<TaskResult<Unit>> = _createOrderState

    private val _ordersByStatus = MutableStateFlow<TaskResult<List<Order>>>(TaskResult.Loading)
    val ordersByStatus: StateFlow<TaskResult<List<Order>>> = _ordersByStatus

    private val _detailOrder = MutableStateFlow<TaskResult<Order>>(TaskResult.Loading)
    val detailOrder: StateFlow<TaskResult<Order>> = _detailOrder

    private val _cancelOrderState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val cancelOrderState: StateFlow<TaskResult<Unit>> = _cancelOrderState

    fun createOrder(order: Order){
        viewModelScope.launch {
            _createOrderState.value = TaskResult.Loading
            val result = createOrderUC(order)
            _createOrderState.value = result
        }
    }
    fun cancelOrder(orderId:String, userId:String, reason:String){
        viewModelScope.launch {
            _cancelOrderState.value = TaskResult.Loading
            val result = cancelledOrderUC(orderId, userId, reason)
            _cancelOrderState.value = result
        }
    }


    fun getUserOrderDetail(orderId: String, userId: String){
        viewModelScope.launch {
            _detailOrder.value = TaskResult.Loading
            val result = getUserOrderDetailUC(orderId, userId)
            _detailOrder.value = result
        }
    }
    fun observeOrdersByStatus(userId: String,  status:String){
        viewModelScope.launch {
            getUserOrdersByStatusUC(userId, status).collect { res->
                _ordersByStatus.value=  res
            }
        }
    }
}
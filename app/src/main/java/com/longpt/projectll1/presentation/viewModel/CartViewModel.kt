package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.usecase.AddToCartUC
import com.longpt.projectll1.domain.usecase.GetCartUC
import com.longpt.projectll1.domain.usecase.RemoveFromCartUC
import com.longpt.projectll1.domain.usecase.UpdateCartItemQuantityUC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val getCartUC: GetCartUC,
    private val addToCartUC: AddToCartUC,
    private val removeFromCartUC: RemoveFromCartUC,
    private val updateCartItemQuantityUC: UpdateCartItemQuantityUC
) : ViewModel() {
    private val _cart = MutableStateFlow<TaskResult<List<CartItem>>>(TaskResult.Loading)
    val cart: StateFlow<TaskResult<List<CartItem>>> = _cart

    private val updateJobs = mutableMapOf<String, Job>()
    private val _addCartState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val addCartState: StateFlow<TaskResult<Unit>> = _addCartState
    private val _removeCartState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val removeCartState: StateFlow<TaskResult<Unit>> = _removeCartState

    fun observeCart(userId: String) {
        viewModelScope.launch {
            getCartUC(userId).collect { result ->
                _cart.value = result
            }
        }
    }

    fun addCart(cartItem: CartItem, userId: String) {
        viewModelScope.launch {
            _addCartState.value = TaskResult.Loading
            val result = addToCartUC(cartItem, userId)
            _addCartState.value = result
        }
    }

    fun removeFromCart(cartItemId: String, userId: String) {
        viewModelScope.launch {
            _removeCartState.value = TaskResult.Loading
            val result = removeFromCartUC(cartItemId, userId)
            _removeCartState.value = result
        }
    }

    fun increaseQuantity(cartItemId: String, userId: String) {
        updateQuantityCartItem(cartItemId, getCartItemQuantity(cartItemId) + 1, userId)
    }

    fun decreaseQuantity(cartItemId: String, userId: String) {
        val current = getCartItemQuantity(cartItemId)
        if (current > 1) updateQuantityCartItem(cartItemId, current - 1, userId)
        if (current == 0) {
            removeFromCart(cartItemId, userId)
        }
    }

    private fun getCartItemQuantity(itemId: String): Int {
        val currentCart = _cart.value
        if (currentCart !is TaskResult.Success) {
            return 0
        }
        val currentCartList = currentCart.data
        val cartItem = currentCartList.find { it.cartItemId == itemId }

        return cartItem?.cartItemQuantity ?: 0
    }

    private fun updateQuantityCartItem(cartItemId: String, newQuantity: Int, userId: String) {
        val currentCart = _cart.value
        if (currentCart !is TaskResult.Success) {
            return
        }
        val currentCartList = currentCart.data
        val newCart = currentCartList.map {
            if (it.cartItemId == cartItemId) {
                it.copy(cartItemQuantity = newQuantity)
            } else {
                it
            }
        }
        _cart.value = TaskResult.Success(newCart)

        updateJobs[cartItemId]?.cancel()
        updateJobs[cartItemId] = viewModelScope.launch(Dispatchers.IO) {
            val result = updateCartItemQuantityUC(cartItemId, newQuantity, userId)
            when (result) {
                is TaskResult.Success -> {}
                is TaskResult.Error -> {}
                else -> { Unit }
            }
        }
    }
}
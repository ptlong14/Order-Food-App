package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.AddToCartUC
import com.longpt.projectll1.domain.usecase.GetCartUC
import com.longpt.projectll1.domain.usecase.RemoveItemFromCartUC
import com.longpt.projectll1.domain.usecase.UpdateCartItemQuantityUC
import com.longpt.projectll1.presentation.viewModel.CartViewModel

class CartViewModelFactory(
    private val getCartUC: GetCartUC,
    private val addToCartUC: AddToCartUC,
    private val removeItemFromCartUC: RemoveItemFromCartUC,
    private val updateCartItemQuantityUC: UpdateCartItemQuantityUC,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(
                getCartUC,
                addToCartUC,
                removeItemFromCartUC,
                updateCartItemQuantityUC
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
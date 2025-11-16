package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.GetFoodByIdUC
import com.longpt.projectll1.presentation.viewModel.FoodDetailViewModel

class FoodDetailViewModelFactory(
    private val getFoodByIdUC: GetFoodByIdUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodDetailViewModel::class.java)) {
            return FoodDetailViewModel(getFoodByIdUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
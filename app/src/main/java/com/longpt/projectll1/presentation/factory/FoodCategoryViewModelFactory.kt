package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.GetFoodsByCategoryUC
import com.longpt.projectll1.presentation.viewModel.FoodCategoryViewModel

class FoodCategoryViewModelFactory(private val useCase: GetFoodsByCategoryUC)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodCategoryViewModel::class.java)) {
            return FoodCategoryViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
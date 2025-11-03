package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.GetBestSellerUC
import com.longpt.projectll1.domain.usecase.GetNewFoodListUC
import com.longpt.projectll1.domain.usecase.GetTopRatedUC
import com.longpt.projectll1.presentation.viewModel.FoodSectionSeeAllViewModel

class FoodSectionSeeAllViewModelFactory(
    val getBestSellerFoodListUC: GetBestSellerUC,
    val getTopRatedFoodListUC: GetTopRatedUC,
    val getNewFoodListUC: GetNewFoodListUC
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodSectionSeeAllViewModel::class.java)) {
            return FoodSectionSeeAllViewModel( getBestSellerFoodListUC, getTopRatedFoodListUC, getNewFoodListUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
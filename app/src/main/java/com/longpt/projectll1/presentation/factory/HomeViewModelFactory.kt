package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.GetAllBannersUC
import com.longpt.projectll1.domain.usecase.GetBestSellerUC
import com.longpt.projectll1.domain.usecase.GetNewFoodListUC
import com.longpt.projectll1.domain.usecase.GetTopRatedUC
import com.longpt.projectll1.presentation.viewModel.HomeViewModel

class HomeViewModelFactory(
    private val getAllBannersUC: GetAllBannersUC,
    private val getBestSellerFoodListUC: GetBestSellerUC,
    private val getTopRatedFoodListUC: GetTopRatedUC,
    private val getNewFoodListUC: GetNewFoodListUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(getAllBannersUC, getBestSellerFoodListUC, getTopRatedFoodListUC, getNewFoodListUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
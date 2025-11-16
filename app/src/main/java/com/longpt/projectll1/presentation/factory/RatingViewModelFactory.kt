package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.AddRatingUC
import com.longpt.projectll1.domain.usecase.GetRatingListByFoodIdUC
import com.longpt.projectll1.presentation.viewModel.RatingOrderViewModel

class RatingViewModelFactory(

    private val addRatingUC: AddRatingUC,
    private val getRatingListByFoodIdUC: GetRatingListByFoodIdUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingOrderViewModel::class.java)) {
            return RatingOrderViewModel(addRatingUC, getRatingListByFoodIdUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
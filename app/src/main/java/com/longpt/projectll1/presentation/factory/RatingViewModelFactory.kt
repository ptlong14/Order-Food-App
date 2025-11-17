package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.AddUpRatingUC
import com.longpt.projectll1.domain.usecase.GetRatingByUserIdUC
import com.longpt.projectll1.domain.usecase.GetRatingListByFoodIdUC
import com.longpt.projectll1.presentation.viewModel.RatingOrderViewModel

class RatingViewModelFactory(

    private val addUpRatingUC: AddUpRatingUC,
    private val getRatingListByFoodIdUC: GetRatingListByFoodIdUC,
    private val getRatingByUserIdUC: GetRatingByUserIdUC,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingOrderViewModel::class.java)) {
            return RatingOrderViewModel(
                addUpRatingUC,
                getRatingListByFoodIdUC,
                getRatingByUserIdUC
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
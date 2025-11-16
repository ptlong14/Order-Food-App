package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.AddToFavoriteUC
import com.longpt.projectll1.domain.usecase.GetFavFoodsUC
import com.longpt.projectll1.domain.usecase.RemoveItemFromFavoriteUC
import com.longpt.projectll1.presentation.viewModel.FavoriteFoodViewModel

class FavFoodsViewModelFactory(
    private val getFavFoodsUC: GetFavFoodsUC,
    private val addFavoriteUC: AddToFavoriteUC,
    private val removeFavoriteUC: RemoveItemFromFavoriteUC,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteFoodViewModel::class.java)) {
            return FavoriteFoodViewModel(getFavFoodsUC, addFavoriteUC, removeFavoriteUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
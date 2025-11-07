package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.SearchFoodsUC
import com.longpt.projectll1.domain.usecase.SyncFoodsDataUC
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.presentation.viewModel.SearchViewModel

class SearchViewModelFactory(
    private val searchFoodsUC: SearchFoodsUC,
    private val syncFoodsDataUC: SyncFoodsDataUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchFoodsUC, syncFoodsDataUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
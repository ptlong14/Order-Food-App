package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.usecase.GetBestSellerUC
import com.longpt.projectll1.domain.usecase.GetNewFoodListUC
import com.longpt.projectll1.domain.usecase.GetTopRatedUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodSectionSeeAllViewModel(
    val getBestSellerFoodListUC: GetBestSellerUC,
    val getTopRatedFoodListUC: GetTopRatedUC,
    val getNewFoodListUC: GetNewFoodListUC
) : ViewModel() {
    private val _bestSeller = MutableStateFlow<TaskResult<List<Food>>>(TaskResult.Loading)
    val bestSeller: StateFlow<TaskResult<List<Food>>> = _bestSeller
    private val _topRated = MutableStateFlow<TaskResult<List<Food>>>(TaskResult.Loading)
    val topRated: StateFlow<TaskResult<List<Food>>> = _topRated
    private val _newFood = MutableStateFlow<TaskResult<List<Food>>>(TaskResult.Loading)
    val newFood: StateFlow<TaskResult<List<Food>>> = _newFood

    fun getBestSeller() {
        viewModelScope.launch {
            _bestSeller.value = TaskResult.Loading
            val result = getBestSellerFoodListUC()
            _bestSeller.value = result
        }
    }
    fun getTopRated() {
        viewModelScope.launch {
            _topRated.value = TaskResult.Loading
            val result = getBestSellerFoodListUC()
            _topRated.value = result
        }
    }
    fun getNewFood() {
        viewModelScope.launch {
            _newFood.value = TaskResult.Loading
            val result = getBestSellerFoodListUC()
            _newFood.value = result
        }
    }

}
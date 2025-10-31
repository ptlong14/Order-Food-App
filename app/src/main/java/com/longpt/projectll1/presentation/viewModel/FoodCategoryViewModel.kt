package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.usecase.GetFoodsByCategoryUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FoodCategoryViewModel(val getFoodsByCategoryUC: GetFoodsByCategoryUC) : ViewModel(){
    private val _foods = MutableStateFlow<TaskResult<List<Food>>>(TaskResult.Loading)
    val foods: StateFlow<TaskResult<List<Food>>> = _foods
    fun getFoodsByCategory(category: String) {
        viewModelScope.launch {
            _foods.value = TaskResult.Loading
            val result = getFoodsByCategoryUC(category)
            _foods.value = result
        }
    }
}
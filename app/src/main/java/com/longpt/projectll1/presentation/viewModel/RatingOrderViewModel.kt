package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.usecase.AddRatingUC
import com.longpt.projectll1.domain.usecase.GetRatingListByFoodIdUC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingOrderViewModel(
    private val addRatingUC: AddRatingUC,
    private val getRatingListByFoodIdUC: GetRatingListByFoodIdUC
) : ViewModel() {

    private val _addRatingResult = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val addRatingResult: StateFlow<TaskResult<Unit>> = _addRatingResult

    private val _ratingList = MutableStateFlow<TaskResult<List<Rating>>>(TaskResult.Loading)
    val ratingList: StateFlow<TaskResult<List<Rating>>> = _ratingList

    fun addRating(rating: Rating, foodId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _addRatingResult.value = TaskResult.Loading
            val result = addRatingUC(rating, foodId, userId)
            _addRatingResult.value = result
        }
    }

    fun getRatingList(foodId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _ratingList.value = TaskResult.Loading
            val result = getRatingListByFoodIdUC(foodId)
            _ratingList.value = result
        }
    }
}
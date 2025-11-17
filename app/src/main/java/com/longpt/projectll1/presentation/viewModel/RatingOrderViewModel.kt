package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.usecase.AddUpRatingUC
import com.longpt.projectll1.domain.usecase.GetRatingByUserIdUC
import com.longpt.projectll1.domain.usecase.GetRatingListByFoodIdUC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingOrderViewModel(
    private val addUpRatingUC: AddUpRatingUC,
    private val getRatingListByFoodIdUC: GetRatingListByFoodIdUC,
    private val getRatingByUserIdUC: GetRatingByUserIdUC
) : ViewModel() {
    private val _addUpRatingResult = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val addUpRatingResult: StateFlow<TaskResult<Unit>> = _addUpRatingResult

    private val _ratingList = MutableStateFlow<TaskResult<List<Rating>>>(TaskResult.Loading)
    val ratingList: StateFlow<TaskResult<List<Rating>>> = _ratingList
    private val _rating = MutableStateFlow<TaskResult<Rating>>(TaskResult.Loading)
    val rating: StateFlow<TaskResult<Rating>> = _rating


    fun addUpRating(rating: Rating, foodId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _addUpRatingResult.value = TaskResult.Loading
            val result = addUpRatingUC(rating, foodId, userId)
            _addUpRatingResult.value = result
        }
    }

    fun getRatingList(foodId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _ratingList.value = TaskResult.Loading
            val result = getRatingListByFoodIdUC(foodId)
            _ratingList.value = result
        }
    }

    fun getRatingByUserId(userId: String, foodId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _rating.value = TaskResult.Loading
            val result = getRatingByUserIdUC(userId, foodId)
            _rating.value = result
        }
    }
}
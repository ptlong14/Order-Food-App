package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.usecase.AddToFavoriteUC
import com.longpt.projectll1.domain.usecase.CheckFoodIsFavoriteUC
import com.longpt.projectll1.domain.usecase.GetFavFoodsUC
import com.longpt.projectll1.domain.usecase.RemoveFromFavoriteUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFoodViewModel(
    val getFavFoodsUC: GetFavFoodsUC,
    val addFavoriteUC: AddToFavoriteUC,
    val removeFavoriteUC: RemoveFromFavoriteUC
): ViewModel() {
    private val _favoriteFoods = MutableStateFlow<TaskResult<List<Food>>>(TaskResult.Loading)
    val favoriteFoods: StateFlow<TaskResult<List<Food>>> = _favoriteFoods

    private val _addFavoriteState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val addFavoriteState: StateFlow<TaskResult<Unit>> = _addFavoriteState

    private val _removeFavoriteState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val removeFavoriteState: StateFlow<TaskResult<Unit>> = _removeFavoriteState
    fun observeFavFoods(userId:String) {
        viewModelScope.launch {
            getFavFoodsUC(userId).collectLatest { result ->
                _favoriteFoods.value = result
            }
        }
    }

    fun isFoodFav(foodId: String):Boolean{
        val res= _favoriteFoods.value
        return res is TaskResult.Success && res.data.any { it.id == foodId }
    }

    fun addFavorite(food: Food, userId: String) {
        viewModelScope.launch {
            _addFavoriteState.value= TaskResult.Loading
            val result = addFavoriteUC(food, userId)
            _addFavoriteState.value = result
        }
    }

    fun removeFavorite(foodId: String, userId: String) {
        viewModelScope.launch {
            _removeFavoriteState.value= TaskResult.Loading
            val result = removeFavoriteUC(foodId, userId)
            _removeFavoriteState.value = result
        }
    }
}
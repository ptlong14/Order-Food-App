package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.usecase.GetFoodByIdUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodDetailViewModel(
    private val getFoodByIdUC: GetFoodByIdUC
) : ViewModel() {
    private val _food = MutableStateFlow<TaskResult<Food>>(TaskResult.Loading)
    val food: StateFlow<TaskResult<Food>> = _food

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    val totalPrice: StateFlow<Double> = combine(_food, _quantity){fTaskResult, q->
        if(fTaskResult is TaskResult.Success){
            val basePrice= fTaskResult.data.price
            val extraPrice = fTaskResult.data.optionGroup.sumOf { g ->
                g.optionItem.filter { it.isChecked }.sumOf { it.extraCost }
            }
            (basePrice + extraPrice) * q
        }else{
            0.0
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)
    private val _canAddToCart = MutableLiveData(true)
    val canAddToCart: LiveData<Boolean> = _canAddToCart
    fun getFoodById(foodId: String) {
        viewModelScope.launch {
            _food.value = TaskResult.Loading
            val result = getFoodByIdUC(foodId)

            if (result is TaskResult.Success) {
                val food = result.data
                food.optionGroup.forEach { group ->
                    if (group.require && group.optionItem.isNotEmpty()) {
                        group.optionItem.first().isChecked = true
                        for (i in 1 until group.optionItem.size) {
                            group.optionItem[i].isEnabled = false
                        }
                    }
                }
                _food.value = TaskResult.Success(food)
            } else {
                _food.value = result
            }
        }
    }

    fun setQuantity(qty: Int) {
        _quantity.value = qty.coerceAtLeast(1)
    }

    fun increaseQuantity() {
        _quantity.value = _quantity.value + 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value  -= 1
        }
    }
    fun toggleOption(groupPos: Int, itemPos: Int) {
        val currentResult = _food.value
        if (currentResult !is TaskResult.Success) return
        val currentFood = currentResult.data

        val newOptionGroups = currentFood.optionGroup.mapIndexed { gIndex, group ->
            if (gIndex != groupPos) return@mapIndexed group

            val newItems = group.optionItem.mapIndexed { iIndex, item ->
                val toggledItem = if (iIndex == itemPos) item.copy(isChecked = !item.isChecked) else item
                toggledItem
            }
            val checkedCount = newItems.count { it.isChecked }

            val finalItems = newItems.map { item ->
                if (checkedCount >= group.maxChoose) item.copy(isEnabled = item.isChecked)
                else item.copy(isEnabled = true)
            }

            group.copy(optionItem = finalItems)
        }

        val newFood = currentFood.copy(optionGroup = newOptionGroups)
        _food.value = TaskResult.Success(newFood)
        checkCanAddToCart(newFood)
    }

    private fun checkCanAddToCart(newFood: Food) {
        val allRequiredChosen = newFood.optionGroup
            .filter { it.require }
            .all { group -> group.optionItem.any { it.isChecked } }
        _canAddToCart.value = allRequiredChosen
    }

    fun getSelectedOptionDescriptions(): List<String> {
        val currentResult = _food.value
        if (currentResult !is TaskResult.Success) return emptyList()
        val food = currentResult.data
        return food.optionGroup.mapNotNull { group ->
            val selected = group.optionItem.filter { it.isChecked }
            if (selected.isNotEmpty()) {
                "${group.groupName}: ${selected.joinToString(", ") { it.optionName }}"
            } else null
        }
    }
}
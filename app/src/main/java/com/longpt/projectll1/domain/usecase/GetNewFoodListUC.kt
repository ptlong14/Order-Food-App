package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.FoodRepository

class GetNewFoodListUC(private val repository: FoodRepository){
    suspend operator fun invoke() = repository.getNewFoodList()
}
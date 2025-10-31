package com.longpt.projectll1.data.modelDTO

import com.google.firebase.Timestamp


data class FoodDto(
    val id: String="",
    val name: String = "",
    val description: String = "",
    val imgUrl: String? = null,
    val category: String = "",
    val optionGroup: List<OptionGroupDto> = emptyList(),
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val sold: Int = 0,
    val createdAt: Timestamp= Timestamp.now()
)

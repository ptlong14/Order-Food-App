package com.longpt.projectll1.data.modelDTO

data class OrderItemDto(
    val orderItemId: String = "",
    val orderFoodName: String = "",
    val foodId: String = "",
    val orderFoodImgUrl: String = "",
    val orderUnitPrice: Double = 0.0,
    val orderItemQuantity: Int = 1,
    val selectedOptions:List<String> = emptyList()
) {
}
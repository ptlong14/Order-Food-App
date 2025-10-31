package com.longpt.projectll1.data.modelDTO

data class CartItemDto(
    val cartItemId: String = "",
    val foodName: String = "",
    val foodImgUrl: String = "",
    val unitPrice: Double = 0.0,
    val cartItemQuantity: Int = 1,
    val selectedOptions:List<String> = emptyList()
) {
}
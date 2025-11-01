package com.longpt.projectll1.data.modelDTO

import com.google.firebase.Timestamp

class OrderDto(
    val orderId: String = "",
    val userId: String = "",
    val orderList: List<OrderItemDto> = emptyList(),
    val addressDto: AddressDto = AddressDto(),
    val totalPrice: Double = 0.0,
    val paymentMethod: String = "",
    val shippingFee: Double = 0.0,
    val orderNote: String = "",
    val orderStatus: String = "Pending",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
}
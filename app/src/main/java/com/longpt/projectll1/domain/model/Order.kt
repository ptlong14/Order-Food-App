package com.longpt.projectll1.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
@Parcelize
data class Order(
    val orderId: String = "",
    val userId: String = "",
    val orderList: List<OrderItem> = emptyList(),
    val address: Address = Address(),
    val totalPrice: Double = 0.0,
    val paymentMethod: String = "",
    val shippingFee: Double = 0.0,
    val orderNote: String = "",
    val orderStatus: String = "Pending",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) : Parcelable
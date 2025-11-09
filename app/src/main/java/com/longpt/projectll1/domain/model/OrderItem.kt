package com.longpt.projectll1.domain.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderItem(
    val orderItemId: String = "",
    val foodId: String = "",
    val orderFoodName: String = "",
    val orderFoodImgUrl: String = "",
    val orderUnitPrice: Double = 0.0,
    val orderItemQuantity: Int = 1,
    val selectedOptions:List<String> = emptyList()
): Parcelable
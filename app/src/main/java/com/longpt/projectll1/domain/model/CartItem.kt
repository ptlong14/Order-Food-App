package com.longpt.projectll1.domain.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val cartItemId: String = "",
    val foodName: String = "",
    val foodImgUrl: String = "",
    val unitPrice: Double = 0.0,
    val cartItemQuantity: Int = 1,
    val selectedOptions:List<String> = emptyList()
) : Parcelable
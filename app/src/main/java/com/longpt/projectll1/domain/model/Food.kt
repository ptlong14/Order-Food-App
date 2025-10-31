package com.longpt.projectll1.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val rating: Double=0.0,
    val imgUrl: String = "",
    val category: String = "",
    val description: String = "",
    val optionGroup: List<OptionGroup> = emptyList(),
    val sold: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
) : Parcelable

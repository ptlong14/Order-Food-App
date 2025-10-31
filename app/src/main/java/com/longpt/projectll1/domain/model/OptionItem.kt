package com.longpt.projectll1.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class OptionItem(
    val optionName:String ="",
    val extraCost:Double =0.0,

    @get:Exclude
    var isChecked:Boolean = false,

    @get:Exclude
    var isEnabled:Boolean = true
): Parcelable
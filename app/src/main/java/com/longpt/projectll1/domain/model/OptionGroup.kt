package com.longpt.projectll1.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OptionGroup(
    val groupName:String ="",
    val maxChoose:Int=0,
    val optionItem: List<OptionItem> = emptyList(),
    val require:Boolean=false
): Parcelable

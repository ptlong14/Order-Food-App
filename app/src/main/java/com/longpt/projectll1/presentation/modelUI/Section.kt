package com.longpt.projectll1.presentation.modelUI

import android.accessibilityservice.GestureDescription
import com.longpt.projectll1.domain.model.Food

data class Section(val type: SectionType, val title: String, val description: String, val foodList: List<Food>, val err: String? = null)
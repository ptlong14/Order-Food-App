package com.longpt.projectll1.utils

object GenerateUtil {
    fun generateCartItemId(foodId: String, optionsText: String): String {
        val key = "$foodId|$optionsText"
        return key.hashCode().toString()
    }
}
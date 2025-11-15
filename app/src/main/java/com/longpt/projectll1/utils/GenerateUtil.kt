package com.longpt.projectll1.utils

import java.util.UUID

object GenerateUtil {
    fun generateCartItemId(foodId: String, optionsText: String): String {
        val key = "$foodId|$optionsText"
        return key.hashCode().toString()
    }
    fun generateUserId(): String = UUID.randomUUID().toString()
}
package com.longpt.projectll1.utils

object FormatUtil {
    fun moneyFormat(amount: Double, pattern: String = "#,### ₫"): String {
        val formatter = java.text.DecimalFormat(pattern)
        return formatter.format(amount)
    }
}
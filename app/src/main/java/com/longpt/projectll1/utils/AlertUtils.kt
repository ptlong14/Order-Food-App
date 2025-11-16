package com.longpt.projectll1.utils

import android.app.AlertDialog
import android.content.Context

object AlertUtils {
    fun showLoginAlert(context: Context, onLoginClick: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Yêu cầu đăng nhập")
            .setMessage("Bạn cần đăng nhập để sử dụng tính năng này.")
            .setPositiveButton("Đăng nhập") { _, _ ->
                onLoginClick()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
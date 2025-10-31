package com.longpt.projectll1.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.longpt.projectll1.databinding.DialogAlertV1Binding


fun String.showToast(context: Context, durationMillis: Long = 2000L) {
    val toast = Toast(context).apply {
        // Inflate layout custom
        val binding = DialogAlertV1Binding.inflate(LayoutInflater.from(context))
        binding.tvMessage.text = this@showToast

        // Set view
        view = binding.root

        // Căn giữa màn hình
        setGravity(Gravity.CENTER, 0, 0)

        // Thời gian hiển thị (ms)
        this.duration = Toast.LENGTH_SHORT  // Dùng mặc định, sẽ tự dismiss bằng Handler
    }

    toast.show()

    // Tự dismiss chính xác theo durationMillis
    Handler(Looper.getMainLooper()).postDelayed({
        toast.cancel()
    }, durationMillis)
}
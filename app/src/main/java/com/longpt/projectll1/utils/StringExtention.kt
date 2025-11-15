package com.longpt.projectll1.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.longpt.projectll1.databinding.DialogAlertV1Binding


fun String.showToast(context: Context, durationMillis: Long = 2700L) {
    val toast = Toast(context).apply {
        val binding = DialogAlertV1Binding.inflate(LayoutInflater.from(context))
        binding.tvMessage.text = this@showToast
        view = binding.root
        setGravity(Gravity.CENTER, 0, 0)
        this.duration = Toast.LENGTH_SHORT
    }

    toast.show()
    Handler(Looper.getMainLooper()).postDelayed({
        toast.cancel()
    }, durationMillis)
}
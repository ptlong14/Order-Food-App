package com.longpt.projectll1.core

import android.app.Application
import com.cloudinary.android.MediaManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        initCloudinary()
    }

    private fun initCloudinary() {
            val config = mapOf("cloud_name" to "djah6ucrq")
            MediaManager.init(this, config)
    }
}
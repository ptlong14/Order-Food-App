package com.longpt.projectll1.presentation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.longpt.projectll1.databinding.CustomToolbarBinding
import com.longpt.projectll1.utils.showToast

class TopBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: CustomToolbarBinding =
        CustomToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        binding.searchViewFood.setOnClickListener {
            val intent = Intent(context, SearchFoodActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
        binding.iBtnCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        binding.iBtnChatWithShop.setOnClickListener {
            "Chat".showToast(context)
        }
    }
}
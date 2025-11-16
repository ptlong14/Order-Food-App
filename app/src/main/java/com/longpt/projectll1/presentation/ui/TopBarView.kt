package com.longpt.projectll1.presentation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.databinding.CustomToolbarBinding
import com.longpt.projectll1.utils.AlertUtils
import com.longpt.projectll1.utils.showToast

class TopBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val currentUser= FirebaseAuth.getInstance().currentUser
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
            if (currentUser!=null) {
                val intent = Intent(context, CartActivity::class.java)
                if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                AlertUtils.showLoginAlert(context) {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.putExtra("from", "client")
                    if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
        }

        binding.iBtnChatWithShop.setOnClickListener {
            "Tính năng này đang được phát triển".showToast(context)
        }
    }
}
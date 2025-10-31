package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.longpt.projectll1.R
import com.longpt.projectll1.databinding.ActivitySearchFoodBinding

class SearchFoodActivity : AppCompatActivity() {
    lateinit var binding : ActivitySearchFoodBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etSearch.requestFocus()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.etSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd =  binding.etSearch.compoundDrawables[2]
                if (drawableEnd != null &&
                    event.rawX >= ( binding.etSearch.right - drawableEnd.bounds.width())) {
                    val query =  binding.etSearch.text.toString().trim()
                    if (query.isNotEmpty()) {
                        Toast.makeText(this, "Tìm kiếm: $query", Toast.LENGTH_SHORT).show()
                    } else {
                      return@setOnTouchListener true
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}
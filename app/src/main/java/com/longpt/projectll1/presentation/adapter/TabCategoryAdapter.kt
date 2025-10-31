package com.longpt.projectll1.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.longpt.projectll1.presentation.ui.FoodsByCategoryFragment

class TabCategoryAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val categories = listOf("snack", "meal", "vegan", "dessert", "drink")
        override fun createFragment(position: Int): Fragment {
     return FoodsByCategoryFragment.newInstance(categories[position])
    }

    override fun getItemCount(): Int {
        return 5
    }
}
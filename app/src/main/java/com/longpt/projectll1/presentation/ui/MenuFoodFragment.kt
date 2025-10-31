package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.longpt.projectll1.R
import com.longpt.projectll1.presentation.adapter.TabCategoryAdapter
import com.longpt.projectll1.databinding.FragmentMenuFoodBinding

class MenuFoodFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    lateinit var binding: FragmentMenuFoodBinding

    private val tabTitles = listOf("Snacks", "Meal", "Vegan", "Dessert", "Drinks")
    private val tabIcons = listOf(
        R.drawable.snack,
        R.drawable.meal,
        R.drawable.vegan,
        R.drawable.dessert,
        R.drawable.drink
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMenuFoodBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        val adapter = TabCategoryAdapter(requireActivity())
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            tab.icon = resources.getDrawable(tabIcons[position])
        }.attach()
    }
}
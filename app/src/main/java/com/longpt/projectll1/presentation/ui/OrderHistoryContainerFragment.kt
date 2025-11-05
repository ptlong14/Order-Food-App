package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.longpt.projectll1.databinding.FragmentOrdersHistoryContainerBinding
import com.longpt.projectll1.presentation.adapter.OrdersViewPagerAdapter

class OrderHistoryContainerFragment : Fragment() {
    lateinit var binding: FragmentOrdersHistoryContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOrdersHistoryContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orderVPAdapter= OrdersViewPagerAdapter(this)
        binding.viewPager.adapter= orderVPAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab,pos->
            when (pos) {
                0 -> tab.text = "Chờ xử lý"
                1 -> tab.text = "Đang giao"
                2 -> tab.text = "Hoàn tất"
                else-> tab.text="Đã hủy"
            }
        }.attach()

        binding.viewPager.isUserInputEnabled = false
    }
}
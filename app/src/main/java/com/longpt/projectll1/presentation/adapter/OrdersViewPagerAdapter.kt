package com.longpt.projectll1.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.longpt.projectll1.presentation.ui.OrderHistoryItemFragment

class OrdersViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun createFragment(position: Int): Fragment {
       return when(position){
           0-> OrderHistoryItemFragment.newInstance("Pending")
           1-> OrderHistoryItemFragment.newInstance("Delivering")
           2-> OrderHistoryItemFragment.newInstance("Completed")
           else-> OrderHistoryItemFragment.newInstance("Cancelled")
       }
    }

    override fun getItemCount(): Int {
        return 4
    }
}
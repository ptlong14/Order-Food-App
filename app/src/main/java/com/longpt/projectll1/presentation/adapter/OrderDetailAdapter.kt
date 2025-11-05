package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvCheckoutBinding
import com.longpt.projectll1.domain.model.OrderItem
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class OrderDetailAdapter (private var orderItems: List<OrderItem>) :
    RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OrderDetailViewHolder {
        val binding =
            ItemRvCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: OrderDetailViewHolder, position: Int
    ) {
        val orderItem = orderItems[position]
        holder.binding.tvProductName.text="${orderItem.orderFoodName} - x${orderItem.orderItemQuantity}"
        holder.binding.tvProductPrice.text = FormatUtil.moneyFormat(orderItem.orderUnitPrice * orderItem.orderItemQuantity)
        val optionList = orderItem.selectedOptions
        val optionString = optionList
            .joinToString(", ") { it.substringAfter(": ").trim() }
        holder.binding.tvOptions.text = optionString
        Glide.with(holder.binding.imgFood.context).load(orderItem.orderFoodImgUrl)
            .into(holder.binding.imgFood)
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }
    fun updateData(newOrderItems: List<OrderItem>) {
        autoUpdateList(
            oldList = orderItems,
            newList = newOrderItems,
            areItemsTheSame = { oldItem, newItem -> oldItem.orderItemId == newItem.orderItemId },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
            onUpdated = {
                orderItems = it
            }
        )
    }

    inner class OrderDetailViewHolder(val binding: ItemRvCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
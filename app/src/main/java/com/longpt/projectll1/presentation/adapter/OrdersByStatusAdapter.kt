package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvOrdersByStatusBinding
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class OrdersByStatusAdapter(
    val type: String,
    var orders: List<Order>,
    var onClickOrderDetailBtn: (String) -> Unit,
    var onClickBtn1: (String, String) -> Unit
) : RecyclerView.Adapter<OrdersByStatusAdapter.OrdersByStatusItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OrdersByStatusItemViewHolder {
        val binding =
            ItemRvOrdersByStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrdersByStatusItemViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: OrdersByStatusItemViewHolder, position: Int
    ) {
        val order = orders[position]
        holder.binding.tvDate.text = order.createdAt.toDate().toString()

        val count = order.orderList.size
        val foods = order.orderList
        val countOfFood = foods.size
        holder.binding.tvTotal.text = "Tổng ($countOfFood món): ${
            FormatUtil.moneyFormat(order.totalPrice + order.shippingFee)
        }"

        if (foods.isNotEmpty()) {
            Glide.with(holder.binding.imgMainFood).load(foods[0].orderFoodImgUrl)
                .into(holder.binding.imgMainFood)
        }

        if (count >= 2) {
            Glide.with(holder.binding.imgSecondFood).load(foods[1].orderFoodImgUrl)
                .into(holder.binding.imgSecondFood)
            holder.binding.imgSecondFood.visibility = View.VISIBLE
        } else holder.binding.imgSecondFood.visibility = View.GONE

        if (count > 2) {
            val numMore = count - 2
            holder.binding.tvItemsSummary.text =
                "${foods[0].orderFoodName} | ${foods[1].orderFoodName} | +$numMore món"
            holder.binding.layoutMoreFoods.visibility = View.VISIBLE
            holder.binding.tvMoreFoods.text = "+$numMore"
            Glide.with(holder.binding.imgMoreFoods).load(foods[2].orderFoodImgUrl)
                .into(holder.binding.imgMoreFoods)
        } else {
            holder.binding.layoutMoreFoods.visibility = View.GONE
            holder.binding.tvItemsSummary.text = when (count) {
                1 -> foods[0].orderFoodName
                2 -> "${foods[0].orderFoodName} | ${foods[1].orderFoodName}"
                else -> ""
            }
        }

        when (type) {
            "Pending" -> {
                holder.binding.btn1.text = "Hủy đơn"
                holder.binding.btn1.setOnClickListener { onClickBtn1(order.orderId, "Cancelled") }
            }

            "Delivering" -> holder.binding.btn1.visibility = View.GONE
            "Completed" -> {
                holder.binding.btn1.text = "Đánh giá"
                holder.binding.btn1.setOnClickListener { onClickBtn1(order.orderId, "Rated") }
            }
            "Cancelled" -> holder.binding.btn1.visibility = View.GONE
        }
        holder.binding.btnOrderDetail.setOnClickListener { onClickOrderDetailBtn(order.orderId) }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun updateData(newData: List<Order>) {
        autoUpdateList(
            oldList = orders,
            newList = newData,
            areItemsTheSame = { old, new -> old.orderId == new.orderId },
            areContentsTheSame = { old, new -> old == new },
            onUpdated = {
                orders = it
            })
    }

    inner class OrdersByStatusItemViewHolder(val binding: ItemRvOrdersByStatusBinding) :
        RecyclerView.ViewHolder(binding.root)
}
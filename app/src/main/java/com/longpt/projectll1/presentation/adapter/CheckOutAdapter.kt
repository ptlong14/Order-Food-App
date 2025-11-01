package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvCheckoutBinding
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.utils.FormatUtil

class CheckOutAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<CheckOutAdapter.CheckOutViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CheckOutViewHolder {
        val binding =
            ItemRvCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckOutViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: CheckOutViewHolder, position: Int
    ) {
        val cartItem = cartItems[position]
        holder.binding.tvProductName.text="${cartItem.foodName} - x${cartItem.cartItemQuantity}"
        holder.binding.tvProductPrice.text = FormatUtil.moneyFormat(cartItem.unitPrice * cartItem.cartItemQuantity)
        val optionList = cartItem.selectedOptions
        val optionString = optionList
            .joinToString(", ") { it.substringAfter(": ").trim() }
        holder.binding.tvOptions.text = optionString
        Glide.with(holder.binding.imgFood.context).load(cartItem.foodImgUrl)
            .into(holder.binding.imgFood)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CheckOutViewHolder(val binding: ItemRvCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
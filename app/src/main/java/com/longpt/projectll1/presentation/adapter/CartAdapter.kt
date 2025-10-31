package com.longpt.projectll1.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvCartBinding
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class CartAdapter(var carts: List<CartItem>,
        val onClickIncrease: (cartItemId: String) -> Unit,
        val onClickDecrease: (cartItemId: String, currentQuantity:Int) -> Unit
    ) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
       val binding= ItemRvCartBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val cart= carts[position]
        holder.binding.tvName.text= cart.foodName
        holder.binding.tvFoodPrice.text= FormatUtil.moneyFormat(cart.unitPrice* cart.cartItemQuantity)
        holder.binding.tvQuantity.text= cart.cartItemQuantity.toString()
        val optionList= cart.selectedOptions
        val optionString = optionList
            .joinToString(", ") { it.substringAfter(": ").trim() }
        holder.binding.tvTopping.text=optionString
        Glide.with(holder.binding.imgFood)
            .load(cart.foodImgUrl)
            .into(holder.binding.imgFood)

        holder.binding.iBtnIncrease.setOnClickListener {
            onClickIncrease(cart.cartItemId)
        }
        holder.binding.iBtnDecrease.setOnClickListener {
            onClickDecrease(cart.cartItemId, cart.cartItemQuantity)
        }
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    fun updateData(newCarts: List<CartItem>) {
        autoUpdateList(
            oldList = carts,
            newList = newCarts,
            areItemsTheSame = { oldItem, newItem -> oldItem.cartItemId == newItem.cartItemId },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
            onUpdated = {
                carts = it
            }
        )
    }
    inner class CartViewHolder(val binding: ItemRvCartBinding) :
        RecyclerView.ViewHolder(binding.root)
}
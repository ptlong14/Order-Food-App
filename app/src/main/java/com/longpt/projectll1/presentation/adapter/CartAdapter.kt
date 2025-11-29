package com.longpt.projectll1.presentation.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.longpt.projectll1.databinding.ItemRvCartBinding
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class CartAdapter(
    var carts: List<CartItem>,
    val onClickIncrease: (cartItemId: String) -> Unit,
    val onClickDecrease: (cartItemId: String, currentQuantity: Int) -> Unit,
    val onSetQuantity: (cartItemId: String, newQuantity: Int) -> Unit,
    val onSwipeCartItem: (cartItemId: String) -> Unit
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val binding = ItemRvCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val cart = carts[position]
        holder.binding.tvName.text = cart.foodName
        holder.binding.tvFoodPrice.text =
            FormatUtil.moneyFormat(cart.unitPrice * cart.cartItemQuantity)
        holder.binding.tvQuantity.setText(cart.cartItemQuantity.toString())
        val optionList = cart.selectedOptions
        val optionString = optionList
            .joinToString(", ") { it.substringAfter(": ").trim() }
        holder.binding.tvTopping.text = optionString
        Glide.with(holder.binding.imgFood)
            .load(cart.foodImgUrl)
            .into(holder.binding.imgFood)

        holder.binding.iBtnIncrease.setOnClickListener {
            onClickIncrease(cart.cartItemId)
        }
        holder.binding.iBtnDecrease.setOnClickListener {
            onClickDecrease(cart.cartItemId, cart.cartItemQuantity)
        }
        holder.binding.tvQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val qty = p0.toString().toIntOrNull() ?: 1
                val clampedQty = qty.coerceIn(1, 99)
                if (clampedQty.toString() != p0.toString()) {
                    holder.binding.tvQuantity.setText(clampedQty.toString())
                    holder.binding.tvQuantity.setSelection(holder.binding.tvQuantity.text.length)
                }
                onSetQuantity(cart.cartItemId, clampedQty)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewBinderHelper.bind(holder.binding.swipeLayout, cart.cartItemId)
        holder.binding.layoutDelete.setOnClickListener {
            onSwipeCartItem(cart.cartItemId)
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
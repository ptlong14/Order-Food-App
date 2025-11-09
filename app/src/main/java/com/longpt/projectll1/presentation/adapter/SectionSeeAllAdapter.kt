package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.R
import com.longpt.projectll1.databinding.ItemRvSectionSeeAllBinding
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class SectionSeeAllAdapter(
    private var foods: List<Food>,
    private var onClickAdd :(String)-> Unit
    ) : RecyclerView.Adapter<SectionSeeAllAdapter.SectionSeeAllItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SectionSeeAllItemViewHolder {
        val binding= ItemRvSectionSeeAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionSeeAllItemViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: SectionSeeAllItemViewHolder,
        position: Int
    ) {
        val food= foods[position]
        holder.binding.tvName.text= food.name
        holder.binding.tvDesc.text= food.description
        holder.binding.tvPrice.text= FormatUtil.moneyFormat(food.price)
        Glide.with(holder.binding.imgProduct)
            .load(food.imgUrl)
            .into(holder.binding.imgProduct)
        val category = food.category
        when (category) {
            "snack" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.im_snack)
            }
            "meal" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.im_meal)
            }
            "vegan" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.im_vegan)
            }
            "dessert" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.im_dessert)
            }
            "drink" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.im_drink)
            }
        }

        holder.binding.tvRating.text= "${food.rating}★"
        holder.binding.iBtnAddToCart.setOnClickListener {
            onClickAdd(food.id)
        }
    }

    override fun getItemCount(): Int {
       return foods.size
    }

    fun updateData(newFoods: List<Food>) {
        autoUpdateList(
            oldList = foods,
            newList = newFoods,
            areItemsTheSame = { old, new -> old.id == new.id },
            areContentsTheSame = { old, new -> old == new },
            onUpdated = {
                foods = it
            }
        )
    }
    inner class SectionSeeAllItemViewHolder(val binding: ItemRvSectionSeeAllBinding) :
        RecyclerView.ViewHolder(binding.root)
}
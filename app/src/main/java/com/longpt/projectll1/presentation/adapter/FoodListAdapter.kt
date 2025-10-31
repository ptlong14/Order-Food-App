package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvFoodBinding
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class FoodListAdapter(
    var foods: List<Food>,
    val onClickFood: (Food) -> Unit
) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {
       val binding= ItemRvFoodBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FoodViewHolder(binding)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(
        holder: FoodViewHolder,
        position: Int
    ) {
        val food= foods[position]
        holder.binding.tvName.text= food.name
        holder.binding.tvRating.text = "${food.rating}★"
        holder.binding.tvPrice.text = FormatUtil.moneyFormat(food.price)
        holder.binding.tvDescription.text= food.description

        Glide.with(holder.binding.imgFood.context)
            .load(food.imgUrl)
            .into(holder.binding.imgFood)
        holder.binding.root.setOnClickListener {
            onClickFood(food)
        }

    }

    override fun getItemCount(): Int {
        return foods.size
    }
    fun updateData(newFoods: List<Food>){
        autoUpdateList(
            oldList = foods,
            newList = newFoods,
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
            onUpdated = {
                foods = it
            }
        )
    }

    inner class FoodViewHolder(val binding: ItemRvFoodBinding) :
        RecyclerView.ViewHolder(binding.root)
}
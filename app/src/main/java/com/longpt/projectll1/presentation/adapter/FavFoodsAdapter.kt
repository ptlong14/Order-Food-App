package com.longpt.projectll1.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.R
import com.longpt.projectll1.databinding.ItemRvFavFoodsBinding
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.utils.autoUpdateList

class FavFoodsAdapter(var favFoods: List<Food>,
                      val onClickFavFood: (Food) -> Unit,
                      val onClickFavIcon: (Food) -> Unit) :
    RecyclerView.Adapter<FavFoodsAdapter.FavFoodsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FavFoodsViewHolder {
        val binding =
            ItemRvFavFoodsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavFoodsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FavFoodsViewHolder, position: Int
    ) {
        val favFood = favFoods[position]
        holder.binding.tvNameFood.text = favFood.name
        Glide.with(holder.binding.imageFood.context).load(favFood.imgUrl)
            .into(holder.binding.imageFood)
        val category = favFood.category
        when (category) {
            "snack" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.snack)
            }
            "meal" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.meal)
            }
            "vegan" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.vegan)
            }
            "dessert" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.dessert)
            }
            "drink" -> {
                holder.binding.iconCategory.setImageResource(R.drawable.drink)
            }
        }

        holder.binding.imageFood.setOnClickListener {
            onClickFavFood(favFood)
        }
        holder.binding.iconFav.setOnClickListener {
            onClickFavIcon(favFood)
        }
    }

    override fun getItemCount(): Int {
        return favFoods.size
    }

    fun updateData(newFoods: List<Food>) {
       autoUpdateList(
           oldList = favFoods,
           newList = newFoods,
           areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
           areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
           onUpdated = {
               favFoods = it
           }
       )
    }

    inner class FavFoodsViewHolder(val binding: ItemRvFavFoodsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
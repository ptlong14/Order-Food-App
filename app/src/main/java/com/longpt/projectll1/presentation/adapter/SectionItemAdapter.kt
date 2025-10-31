package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvSectionItemBinding
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.utils.FormatUtil

class SectionItemAdapter(
    val foodSections: List<Food>,
    val onClickCart: (Food) -> Unit,
    val onClickItem: (Food)-> Unit
): RecyclerView.Adapter<SectionItemAdapter.SectionItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SectionItemViewHolder {
       val binding= ItemRvSectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionItemViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: SectionItemViewHolder,
        position: Int
    ) {
        val food= foodSections[position]
        holder.binding.textViewName.text= food.name
        holder.binding.textViewStar.text= "${food.rating}★"
        holder.binding.tvPrice.text= FormatUtil.moneyFormat(food.price)
        Glide.with(holder.binding.imageView.context)
            .load(food.imgUrl)
            .into(holder.binding.imageView)

        holder.binding.iBtnAddToCart.setOnClickListener {
            onClickCart(food)
        }
        holder.binding.imgNameFood.setOnClickListener {

            onClickItem(food)
        }
    }

    override fun getItemCount(): Int {
        return foodSections.size
    }
    inner class SectionItemViewHolder(val binding: ItemRvSectionItemBinding):
            RecyclerView.ViewHolder(binding.root)
}

package com.longpt.projectll1.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvSearchResultBinding
import com.longpt.projectll1.presentation.modelUI.FoodsSearchResult
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class SearchResultAdapter(var listResult: List<FoodsSearchResult>,
    val onClickFood: (String) -> Unit
    ): RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultViewHolder {
        val binding= ItemRvSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SearchResultViewHolder,
        position: Int
    ) {
        val result= listResult[position]
        holder.binding.tvName.text= result.name
        holder.binding.tvPrice.text= FormatUtil.moneyFormat(result.price.toDouble())
        Glide.with(holder.itemView.context).load(result.imgUrl).into(holder.binding.imgFood)
        holder.binding.layoutContent.setOnClickListener {
            onClickFood(result.id)
        }
    }

    override fun getItemCount(): Int {
      return listResult.size
    }

    fun updateData(newData: List<FoodsSearchResult>) {
        autoUpdateList(
            oldList = listResult,
            newList = newData,
            areItemsTheSame = { old, new -> old.id == new.id },
            areContentsTheSame = { old, new -> old == new },
            onUpdated = {
                listResult= it
            }
        )
    }
    inner class SearchResultViewHolder(val binding: ItemRvSearchResultBinding): RecyclerView.ViewHolder(binding.root)
}
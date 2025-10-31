package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.longpt.projectll1.databinding.ItemRvOptionGroupBinding
import com.longpt.projectll1.domain.model.OptionGroup
import com.longpt.projectll1.utils.autoUpdateList

class OptionGroupAdapter(
    private var optionGroups: List<OptionGroup>,
    private val onOptionItemChecked: (groupPosition: Int, itemPosition: Int) -> Unit
) : RecyclerView.Adapter<OptionGroupAdapter.OptionGroupViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionGroupViewHolder {
        val binding = ItemRvOptionGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OptionGroupViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionGroupViewHolder, position: Int) {
        val optionGroup = optionGroups[position]
        holder.binding.tvGroupName.text = if (optionGroup.require) {
            "*${optionGroup.groupName} (Chọn tối đa ${optionGroup.maxChoose})"
        } else {
            "${optionGroup.groupName} (Chọn tối đa ${optionGroup.maxChoose})"
        }

        holder.binding.rvOptionItems.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setRecycledViewPool(viewPool)
            adapter = OptionItemAdapter(optionGroup.optionItem) { itemPosition ->
                onOptionItemChecked(position, itemPosition)
            }
        }
    }

    override fun getItemCount(): Int = optionGroups.size
    fun updateData(newGroups: List<OptionGroup>) {
        autoUpdateList(
            oldList = optionGroups,
            newList = newGroups,
            areItemsTheSame = { oldItem, newItem -> oldItem.groupName == newItem.groupName },
            areContentsTheSame = { oldItem, newItem ->
                oldItem.optionItem == newItem.optionItem
            },
            onUpdated = {
                optionGroups = it
            })
    }

    inner class OptionGroupViewHolder(val binding: ItemRvOptionGroupBinding) :
        RecyclerView.ViewHolder(binding.root)
}
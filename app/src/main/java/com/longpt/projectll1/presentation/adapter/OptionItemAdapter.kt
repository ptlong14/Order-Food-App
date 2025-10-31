package com.longpt.projectll1.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.longpt.projectll1.databinding.ItemRvOptionItemBinding
import com.longpt.projectll1.domain.model.OptionItem
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.autoUpdateList

class OptionItemAdapter(
    var options: List<OptionItem>, val onCheckBoxClick: (Int) -> Unit
) : RecyclerView.Adapter<OptionItemAdapter.OptionItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionItemViewHolder {
        val binding =
            ItemRvOptionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OptionItemViewHolder, position: Int
    ) {
        val optionItem = options[position]
        val checkBox = holder.binding.cbOptionItem
        checkBox.setOnCheckedChangeListener(null)

        checkBox.text = FormatUtil.moneyFormat(optionItem.extraCost)
        holder.binding.tvOptionName.text = optionItem.optionName

        checkBox.isChecked = optionItem.isChecked
        checkBox.isEnabled = optionItem.isEnabled

        checkBox.setOnCheckedChangeListener { _, _ ->
            onCheckBoxClick(position)
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }

    inner class OptionItemViewHolder(val binding: ItemRvOptionItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
package com.longpt.projectll1.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvSectionBinding
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.presentation.modelUI.Section
import com.longpt.projectll1.presentation.modelUI.SectionType
import com.longpt.projectll1.presentation.modelUI.description

class SectionAdapter(
    private val sections: MutableList<Section>,
    private val onSeeAllClick: (SectionType) -> Unit,
    private val onClickCart: (Food) -> Unit,
    private val onClickItem: (Food)-> Unit
): RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SectionViewHolder {
        val binding= ItemRvSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SectionViewHolder,
        position: Int
    ) {
        val section = sections[position]
        holder.binding.tvSectionTitle.text= section.title
        holder.binding.tvSectionDescription.text= section.description
        if (section.err != null) {
            holder.binding.sectionError.visibility = View.VISIBLE
            holder.binding.sectionError.text = section.err
            holder.binding.rvSectionItem.visibility = View.INVISIBLE
            holder.binding.tvSeeAll.visibility= View.INVISIBLE

        } else if (section.foodList.isNotEmpty()) {
            holder.binding.sectionError.visibility = View.GONE
            holder.binding.rvSectionItem.adapter = SectionItemAdapter(
                section.foodList.take(5),
                onClickCart,
                onClickItem
            )
            holder.binding.rvSectionItem.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.binding.tvSeeAll.setOnClickListener {
                onSeeAllClick(section.type)
            }
        } else {
            holder.binding.sectionError.visibility = View.VISIBLE
            holder.binding.sectionError.text = "Không có món ăn nào"
            holder.binding.rvSectionItem.visibility = View.INVISIBLE
           holder.binding.tvSeeAll.visibility= View.INVISIBLE
        }

       holder.binding.rvSectionItem.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val swipe = recyclerView.findParentSwipeRefreshLayout()
                val isIdle = newState == RecyclerView.SCROLL_STATE_IDLE
                swipe?.isEnabled = isIdle
            }
        })
    }

    override fun getItemCount(): Int {
       return sections.size
    }
    fun updateData(newData: List<Section>) {
        sections.clear()
        sections.addAll(newData)
        notifyDataSetChanged()
    }
    private fun View.findParentSwipeRefreshLayout(): SwipeRefreshLayout? {
        var view: View? = this
        while (view != null) {
            if (view is SwipeRefreshLayout) return view
            view = view.parent as? View
        }
        return null
    }
    inner class SectionViewHolder(val binding: ItemRvSectionBinding):
            RecyclerView.ViewHolder(binding.root)
}
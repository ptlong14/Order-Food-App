package com.longpt.projectll1.presentation.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.longpt.projectll1.databinding.ItemRvRatingBinding
import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.utils.autoUpdateList

class RatingAdapter(private var ratingList: List<Rating>) :
    RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RatingViewHolder {
        val binding =
            ItemRvRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RatingViewHolder,
        position: Int
    ) {
        val rating = ratingList[position]

        holder.binding.apply {
            tvUserName.text = rating.userName
            tvComment.text = rating.comment
            ratingBar.rating = rating.rating.toFloat()
            tvDate.text = rating.updatedAt.toDate().toString()
            if (rating.avatarUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context).load(rating.avatarUrl).into(imgAvatar)
            }
        }
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }

    fun updateData(newList: List<Rating>) {
        autoUpdateList(
            oldList = ratingList,
            newList = newList,
            areItemsTheSame = { oldItem, newItem -> oldItem.userId == newItem.userId },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
            onUpdated = {
                ratingList = it
            }
        )
    }

    inner class RatingViewHolder(val binding: ItemRvRatingBinding) :
        RecyclerView.ViewHolder(binding.root)
}
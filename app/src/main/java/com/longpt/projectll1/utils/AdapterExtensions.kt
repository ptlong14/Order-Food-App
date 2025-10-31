package com.longpt.projectll1.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

fun <T> RecyclerView.Adapter<*>.autoUpdateList(
    oldList: List<T>,
    newList: List<T>,
    areItemsTheSame: (T, T) -> Boolean,
    areContentsTheSame: (T, T) -> Boolean,
    onUpdated: (List<T>) -> Unit
) {
    val diffCallback = object : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
        }
    }

    val diffResult = DiffUtil.calculateDiff(diffCallback)
    onUpdated(newList)
    diffResult.dispatchUpdatesTo(this)
}
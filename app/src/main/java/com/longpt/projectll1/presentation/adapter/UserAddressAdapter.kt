package com.longpt.projectll1.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.longpt.projectll1.databinding.ItemRvAddressBinding
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.utils.autoUpdateList


class UserAddressAdapter(var addressList: List<Address>,
    val onClickAddress: (Address) -> Unit
    ) :
    RecyclerView.Adapter<UserAddressAdapter.UserAddressViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): UserAddressViewHolder {
        val binding =
            ItemRvAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserAddressViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: UserAddressViewHolder, position: Int
    ) {
        val address = addressList[position]
        holder.binding.tvReceiverInfo.text = "${address.receiverName} | ${address.phoneNumber}"
        holder.binding.tvFullAddress.text = address.fullAddress
        if (address.defaultAddress) {
            holder.binding.tvDefaultLabel.apply {
                visibility = View.VISIBLE
                text = "Mặc định"
            }
        } else {
            holder.binding.tvDefaultLabel.visibility = View.GONE
        }
        holder.binding.llInfo.setOnClickListener {
            onClickAddress(address)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun updateData(newList: List<Address>) {
        autoUpdateList(
            oldList = addressList,
            newList = newList,
            areItemsTheSame = { oldItem, newItem -> oldItem.addressId == newItem.addressId },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
            onUpdated = {
                addressList = it
            })
    }

    inner class UserAddressViewHolder(val binding: ItemRvAddressBinding) :
        RecyclerView.ViewHolder(binding.root)
}
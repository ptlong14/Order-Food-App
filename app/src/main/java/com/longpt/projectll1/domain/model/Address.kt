package com.longpt.projectll1.domain.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressId: String="",
    val fullAddress: String="",
    val addressType: String="",
    val phoneNumber: String="",
    val receiverName: String="",
    val latitude: Double=0.0,
    val longitude: Double=0.0,
    var defaultAddress: Boolean=true,
    val createdAt: Timestamp= Timestamp.now()
):Parcelable
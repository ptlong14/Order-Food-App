package com.longpt.projectll1.data.modelDTO

import com.google.firebase.Timestamp

data class AddressDto(
    val addressId: String="",
    val fullAddress: String="",
    val addressType: String="",
    val phoneNumber: String="",
    val receiverName: String="",
    val latitude: Double=0.0,
    val longitude: Double=0.0,
    var defaultAddress: Boolean=true,
    val createdAt: Timestamp= Timestamp.now()
){

}
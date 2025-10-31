package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.AddressRepository

class GetAddressByIdUC(private val repository: AddressRepository){
    suspend operator fun invoke(addressId: String, userId: String) = repository.getAddressById(addressId, userId)
}
package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.repository.AddressRepository

class UpdateAddressByIdUC(private val addressRepository: AddressRepository) {
    suspend operator fun invoke(updateAddress: Address, userId: String)= addressRepository.updateAddress(updateAddress, userId)
}
package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.AddressRepository

class ChangeDefaultAddressUC(private val addressRepository: AddressRepository) {
    suspend operator fun invoke(addressId: String, userId: String)= addressRepository.changeDefaultAddress(addressId, userId)
}
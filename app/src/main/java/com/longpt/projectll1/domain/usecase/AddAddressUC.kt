package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.repository.AddressRepository

class AddAddressUC(val addressRepository: AddressRepository) {
    suspend operator fun invoke(address: Address, userId: String) = addressRepository.addAddress(address, userId)
}
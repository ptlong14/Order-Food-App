package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.AddressRepository

class GetAddressesUC(val addressRepository: AddressRepository) {
    operator fun invoke(userId: String) = addressRepository.getAddressList(userId)
}
package com.longpt.projectll1.data.mapper

import com.longpt.projectll1.data.modelDTO.AddressDto
import com.longpt.projectll1.domain.model.Address

object AddressMapper {
    fun fromDtoToDomain(dto: AddressDto): Address {
        return Address(
            addressId = dto.addressId,
            fullAddress = dto.fullAddress,
            addressType = dto.addressType,
            phoneNumber = dto.phoneNumber,
            receiverName = dto.receiverName,
            latitude = dto.latitude,
            longitude = dto.longitude,
            defaultAddress = dto.defaultAddress,
            createdAt = dto.createdAt
        )
    }

    fun fromDomainToDto(domain: Address): AddressDto {
        return AddressDto(
            addressId = domain.addressId,
            fullAddress = domain.fullAddress,
            addressType = domain.addressType,
            phoneNumber = domain.phoneNumber,
            receiverName = domain.receiverName,
            latitude = domain.latitude,
            longitude = domain.longitude,
            defaultAddress = domain.defaultAddress,
            createdAt = domain.createdAt
        )
    }
}
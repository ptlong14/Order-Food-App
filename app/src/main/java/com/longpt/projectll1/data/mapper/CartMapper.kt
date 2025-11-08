package com.longpt.projectll1.data.mapper

import com.longpt.projectll1.data.modelDTO.CartItemDto
import com.longpt.projectll1.domain.model.CartItem

object CartMapper {
    fun fromDtoToDomain(dto: CartItemDto): CartItem {
        return CartItem(
            cartItemId = dto.cartItemId,
            foodName = dto.foodName,
            foodImgUrl = dto.foodImgUrl,
            unitPrice = dto.unitPrice,
            cartItemQuantity = dto.cartItemQuantity,
            selectedOptions = dto.selectedOptions
        )
    }

    fun fromDomainToDto(domain: CartItem): CartItemDto {
        return CartItemDto(
            cartItemId = domain.cartItemId,
            foodName = domain.foodName,
            foodImgUrl = domain.foodImgUrl,
            unitPrice = domain.unitPrice,
            cartItemQuantity = domain.cartItemQuantity,
            selectedOptions = domain.selectedOptions
        )
    }

    fun toCartItems(listCartItemDto: List<CartItemDto>): List<CartItem> {
        val mapped = listCartItemDto.map {
            CartItem(
                cartItemId = it.cartItemId,
                foodName = it.foodName,
                foodImgUrl = it.foodImgUrl,
                unitPrice = it.unitPrice,
                cartItemQuantity = it.cartItemQuantity,
                selectedOptions = it.selectedOptions
            )
        }
        return mapped
    }
}
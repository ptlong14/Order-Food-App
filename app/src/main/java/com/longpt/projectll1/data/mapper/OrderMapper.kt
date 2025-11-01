package com.longpt.projectll1.data.mapper

import com.longpt.projectll1.data.modelDTO.OrderDto
import com.longpt.projectll1.data.modelDTO.OrderItemDto
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.model.OrderItem

object OrderMapper {
    fun fromDtoToDomain(dto: OrderDto): Order {
        val orderListMapped = dto.orderList.takeIf { it.isNotEmpty() }?.map {
            OrderItem(
                orderItemId = it.orderItemId,
                orderFoodName = it.orderFoodName,
                orderFoodImgUrl = it.orderFoodImgUrl,
                orderUnitPrice = it.orderUnitPrice,
                orderItemQuantity = it.orderItemQuantity,
                selectedOptions = it.selectedOptions
            )
        } ?: emptyList()
        return Order(
            orderId = dto.orderId,
            userId = dto.userId,
            orderList = orderListMapped,
            address = AddressMapper.fromDtoToDomain(dto.addressDto),
            totalPrice = dto.totalPrice,
            paymentMethod = dto.paymentMethod,
            shippingFee = dto.shippingFee,
            orderNote = dto.orderNote,
            orderStatus = dto.orderStatus,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun fromDomainToDto(domain: Order): OrderDto {
        val orderListMapped = domain.orderList.takeIf { it.isNotEmpty() }?.map {
                OrderItemDto(
                    orderItemId = it.orderItemId,
                    orderFoodName = it.orderFoodName,
                    orderFoodImgUrl = it.orderFoodImgUrl,
                    orderUnitPrice = it.orderUnitPrice,
                    orderItemQuantity = it.orderItemQuantity,
                    selectedOptions = it.selectedOptions
                )
            } ?: emptyList()
        return OrderDto(
            orderId = domain.orderId,
            userId = domain.userId,
            orderList = orderListMapped,
            addressDto = AddressMapper.fromDomainToDto(domain.address),
            totalPrice = domain.totalPrice,
            paymentMethod = domain.paymentMethod,
            shippingFee = domain.shippingFee,

            orderNote = domain.orderNote,
            orderStatus = domain.orderStatus,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
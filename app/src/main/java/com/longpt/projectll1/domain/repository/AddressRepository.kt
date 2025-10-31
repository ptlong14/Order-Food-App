package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getAddressList(userId: String): Flow<TaskResult<List<Address>>>
    suspend fun addAddress(address: Address, userId: String): TaskResult<Unit>
    suspend fun updateAddress(updateAddress: Address, userId: String): TaskResult<Unit>
    suspend fun deleteAddress(addressId: String, userId: String): TaskResult<Unit>
    suspend fun changeDefaultAddress(addressId: String, userId: String): TaskResult<Unit>
    suspend fun getAddressById(addressId: String, userId: String): TaskResult<Address>
}

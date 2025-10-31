package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.AddressMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddressRepositoryImpl(private val dataSource: FirestoreDataSource) : AddressRepository {
    override fun getAddressList(userId: String): Flow<TaskResult<List<Address>>> {
        return dataSource.getAddresses(userId).map { result ->
            when (result) {
                is TaskResult.Loading -> TaskResult.Loading
                is TaskResult.Error -> TaskResult.Error(result.exception)
                is TaskResult.Success -> {
                    val mapped = result.data.map { dto -> AddressMapper.fromDtoToDomain(dto) }
                    TaskResult.Success(mapped)
                }
            }
        }
    }

    override suspend fun addAddress(
        address: Address, userId: String
    ): TaskResult<Unit> {
        val dto = AddressMapper.fromDomainToDto(address)
        return dataSource.addAddress(dto, userId)
    }

    override suspend fun updateAddress(
        updateAddress: Address, userId: String
    ): TaskResult<Unit> {
        val dto = AddressMapper.fromDomainToDto(updateAddress)
        return dataSource.updateAddress(dto, userId)
    }

    override suspend fun deleteAddress(
        addressId: String, userId: String
    ): TaskResult<Unit> {
        return dataSource.deleteAddress(addressId, userId)
    }

    override suspend fun changeDefaultAddress(
        addressId: String, userId: String
    ): TaskResult<Unit> {
        return dataSource.changeAddress(addressId, userId)
    }

    override suspend fun getAddressById(
        addressId: String, userId: String
    ): TaskResult<Address> {
        return when (val res = dataSource.getAddressById(addressId, userId)) {
            is TaskResult.Success -> {
                val mapped = AddressMapper.fromDtoToDomain(res.data)
                TaskResult.Success(mapped)
            }
            is TaskResult.Error -> TaskResult.Error(res.exception)
            is TaskResult.Loading -> TaskResult.Loading
        }
    }
}
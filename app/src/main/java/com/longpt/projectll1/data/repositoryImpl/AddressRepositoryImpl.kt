package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.AddressMapper
import com.longpt.projectll1.data.mapper.FoodMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddressRepositoryImpl(private val dataSource: FirestoreDataSource): AddressRepository {
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
        address: Address,
        userId: String
    ): TaskResult<Unit> {
        val dto = AddressMapper.fromDomainToDto(address)
        return dataSource.addAddress(dto, userId)
    }

    override suspend fun updateAddress(
        address: Address,
        userId: String
    ): TaskResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAddress(
        addressId: String,
        userId: String
    ): TaskResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun changeDefaultAddress(
        addressId: String,
        userId: String
    ): TaskResult<Unit> {
        return dataSource.changeAddress(addressId, userId)
    }
}
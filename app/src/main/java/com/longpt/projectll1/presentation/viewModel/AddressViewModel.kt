package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.DeleteAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.domain.usecase.UpdateAddressByIdUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    val addAddressUC: AddAddressUC,
    val updateAddressByIdUC: UpdateAddressByIdUC,
    val deleteAddressByIdUC: DeleteAddressByIdUC,
    val getAddressesUC: GetAddressesUC,
    val getAddressByIdUC: GetAddressByIdUC
) : ViewModel() {
    private val _addresses = MutableStateFlow<TaskResult<List<Address>>>(TaskResult.Loading)
    val addresses: StateFlow<TaskResult<List<Address>>> = _addresses

    private val _addAddrState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val addAddrState: StateFlow<TaskResult<Unit>> = _addAddrState

    private val _updateAddrState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val updateAddrState: StateFlow<TaskResult<Unit>> = _updateAddrState

    private val _deleteAddrState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val deleteAddrState: StateFlow<TaskResult<Unit>> = _deleteAddrState
    private val _changeAddrState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val changeAddrState: StateFlow<TaskResult<Unit>> = _changeAddrState

    private val _addressById = MutableStateFlow<TaskResult<Address>>(TaskResult.Loading)
    val addressById: StateFlow<TaskResult<Address>> = _addressById

    fun observeAddresses(userId: String) {
        viewModelScope.launch {
            getAddressesUC(userId).collect { result ->
                _addresses.value = result
            }
        }
    }

    fun addAddress(address: Address, userId: String) {
        viewModelScope.launch {
            _addAddrState.value = TaskResult.Loading
            val result = addAddressUC(address, userId)
            _addAddrState.value = result
        }
    }

    fun updateAddress(updateAddress: Address, userId: String) {
        viewModelScope.launch {
            _updateAddrState.value = TaskResult.Loading
            val result = updateAddressByIdUC(updateAddress, userId)
            _updateAddrState.value = result
        }
    }

    fun deleteAddress(addressId: String, userId: String) {
        viewModelScope.launch {
            _deleteAddrState.value = TaskResult.Loading
            val result = deleteAddressByIdUC(addressId, userId)
            _deleteAddrState.value = result
        }
    }
    fun getAddressById(userId: String, addressId: String) {
        viewModelScope.launch {
            _addressById.value = TaskResult.Loading
            val result = getAddressByIdUC(addressId, userId)
            _addressById.value = result
        }
    }
}
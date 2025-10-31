package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.ChangeDefaultAddressUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddressViewModel(
    val addAddressUC: AddAddressUC,
    val getAddressesUC: GetAddressesUC,
    val changeDefaultAddressUC: ChangeDefaultAddressUC
): ViewModel() {
    private val _addresses = MutableStateFlow<TaskResult<List<Address>>>(TaskResult.Loading)
    val addresses: StateFlow<TaskResult<List<Address>>> = _addresses

    private val _addAddrState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val addAddrState: StateFlow<TaskResult<Unit>> = _addAddrState

    private val _changeAddrState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val changeAddrState: StateFlow<TaskResult<Unit>> = _changeAddrState


    val defaultAddress: StateFlow<String> = _addresses.map { result ->
        when(result) {
            is TaskResult.Loading -> "Đang tải..."
            is TaskResult.Error -> "Lỗi tải địa chỉ"
            is TaskResult.Success -> {
                val addr = result.data.firstOrNull { it.defaultAddress }
                    ?: result.data.firstOrNull()
                if (addr != null) "${addr.receiverName} | ${addr.phoneNumber}\n${addr.fullAddress}"
                else "Chưa có địa chỉ"
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, "Chưa có địa chỉ")
    fun observeAddresses(userId: String) {
        viewModelScope.launch {
            getAddressesUC(userId).collect { result ->
                _addresses.value = result
            }
        }
    }

    fun addAddr(address: Address, userId: String) {
        viewModelScope.launch {
            _addAddrState.value = TaskResult.Loading
            val result = addAddressUC(address, userId)
            _addAddrState.value = result
        }
    }
    fun changeDefaultAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            _changeAddrState.value = TaskResult.Loading
            val result = changeDefaultAddressUC(addressId, userId)
            _changeAddrState.value = result
        }
    }
}
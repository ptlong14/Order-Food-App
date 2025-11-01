package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.ChangeDefaultAddressUC
import com.longpt.projectll1.domain.usecase.DeleteAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.domain.usecase.UpdateAddressByIdUC
import com.longpt.projectll1.presentation.viewModel.AddressViewModel

class AddressViewModelFactory(
    private val getAddressUC: GetAddressesUC,
    private val addAddressUC: AddAddressUC,
    private val updateAddressByIdUC: UpdateAddressByIdUC,
    private val deleteAddressByIdUC: DeleteAddressByIdUC,
    private val getAddressByIdUC: GetAddressByIdUC
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            return AddressViewModel(addAddressUC,updateAddressByIdUC,deleteAddressByIdUC,getAddressUC, getAddressByIdUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
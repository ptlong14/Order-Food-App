package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.AddressRepositoryImpl
import com.longpt.projectll1.databinding.ActivityUpdateAddressBinding
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.DeleteAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.domain.usecase.UpdateAddressByIdUC
import com.longpt.projectll1.presentation.factory.AddressViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AddressViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class UpdateAddressActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateAddressBinding
    private lateinit var addressViewModel: AddressViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid
    private lateinit var mode: String
    private var lat: Double? = null
    private var lng: Double? = null
    private var isDefaultAddr: Boolean = false

    private val mapLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == RESULT_OK) {
                val selectedLat = res.data?.getDoubleExtra("lat", 0.0)
                val selectedLng = res.data?.getDoubleExtra("lng", 0.0)
                if (selectedLat != null && selectedLng != null) {
                    lat = selectedLat
                    lng = selectedLng
                    val fullAddr = res.data?.getStringExtra("fullAddress") ?: "Default"
                    binding.tvSelectedAddress.text = fullAddr
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //ViewModel
        val repoAddr = AddressRepositoryImpl(FirestoreDataSource())
        val getAddressUC = GetAddressesUC(repoAddr)
        val addAddressUC = AddAddressUC(repoAddr)
        val updateAddressByIdUC = UpdateAddressByIdUC(repoAddr)
        val deleteAddressByIdUC = DeleteAddressByIdUC(repoAddr)
        val getAddressByIdUC = GetAddressByIdUC(repoAddr)

        val addressFactory = AddressViewModelFactory(
            getAddressUC, addAddressUC, updateAddressByIdUC, deleteAddressByIdUC, getAddressByIdUC
        )
        addressViewModel = ViewModelProvider(this, addressFactory)[AddressViewModel::class.java]

        //Mode
        mode = intent.getStringExtra("mode") ?: "add"

        if (mode == "add") {
            binding.tvLabelAddOrUpdateAddr.text = "Thêm địa chỉ"
            binding.btnDeleteAddress.visibility = View.GONE
        } else if (mode == "edit") {
            binding.tvLabelAddOrUpdateAddr.text = "Sửa địa chỉ"
            binding.btnDeleteAddress.visibility = View.VISIBLE
            binding.btnSaveAddress.text = "Cập nhật"

            val addrId = intent.getStringExtra("addressId")
            if (addrId != null) {
                addressViewModel.getAddressById(userId, addrId)

                lifecycleScope.launch {
                    addressViewModel.addressById.collect { res ->
                        when (res) {
                            is TaskResult.Loading -> {}
                            is TaskResult.Error -> {
                                res.exception.message?.showToast(this@UpdateAddressActivity)
                            }

                            is TaskResult.Success -> {
                                val addr = res.data
                                binding.edtFullName.setText(addr.receiverName)
                                binding.edtPhone.setText(addr.phoneNumber)
                                binding.tvSelectedAddress.text = (addr.fullAddress)
                                lat = addr.latitude
                                lng = addr.longitude
                                isDefaultAddr = addr.defaultAddress
                                if(isDefaultAddr){
                                    binding.cbDefaultAddress.isChecked = true
                                    binding.cbDefaultAddress.setOnClickListener {
                                        val mess= "Để hủy địa chỉ mặc định này, vui lòng chọn địa chỉ khác làm địa chỉ mặc định mới!"
                                        mess.showToast(this@UpdateAddressActivity)
                                        binding.cbDefaultAddress.isChecked = true
                                    }
                                }
                                if (addr.addressType == "Nhà riêng") {
                                    binding.rbHome.isChecked = true
                                } else {
                                    binding.rbOffice.isChecked = true
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.layoutChooseAddr.setOnClickListener {
            val intent = Intent(this, ChooseLocationActivity::class.java)
            intent.putExtra("mode", mode)
            if (lat != null && lng != null) {
                intent.putExtra("lat", lat)
                intent.putExtra("lng", lng)
            }
            mapLauncher.launch(intent)
        }


        binding.btnDeleteAddress.setOnClickListener {
            val addrId = intent.getStringExtra("addressId")
            if (addrId != null) {
                AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn xóa địa chỉ này?")
                    .setPositiveButton("Có") { _, _ ->
                        addressViewModel.deleteAddress(addrId, userId)
                        lifecycleScope.launch {
                            addressViewModel.deleteAddrState.collect { res ->
                                when (res) {
                                    is TaskResult.Loading -> {}
                                    is TaskResult.Error -> {
                                        res.exception.message?.showToast(this@UpdateAddressActivity)
                                    }

                                    is TaskResult.Success -> {
                                        "Xóa địa chỉ thành công!".showToast(this@UpdateAddressActivity)
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                    .setNegativeButton("Không", null)
                    .show()
            }
        }

        binding.btnSaveAddress.setOnClickListener {
            val name = binding.edtFullName.text.toString()
            val phone = binding.edtPhone.text.toString()
            val addr = binding.tvSelectedAddress.text.toString()
            val isChooseDefault = binding.cbDefaultAddress.isChecked

            if (name.isEmpty()) {
                binding.edtFullName.error = "Vui lòng nhập tên"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                binding.edtPhone.error = "Vui lòng nhập số điện thoại"
                return@setOnClickListener
            }
            val addrType = if (binding.rbHome.isChecked) "Nhà riêng" else "Văn phòng"
            if (lat == null || lng == null) {
                "Vui lòng chọn vị trí trên bản đồ".showToast(this)
                return@setOnClickListener
            } else {
                when (mode) {
                    "add" -> {
                        addressViewModel.addAddress(
                            Address(
                                addressId = "",
                                receiverName = name,
                                phoneNumber = phone,
                                fullAddress = addr,
                                addressType = addrType,
                                defaultAddress = isChooseDefault,
                                latitude = lat!!,
                                longitude = lng!!
                            ), userId
                        )

                        lifecycleScope.launch {
                            addressViewModel.addAddrState.collect { res ->
                                when (res) {
                                    is TaskResult.Loading -> {}
                                    is TaskResult.Error -> {
                                        res.exception.message?.showToast(this@UpdateAddressActivity)
                                    }

                                    is TaskResult.Success -> {
                                        "Thêm địa chỉ thành công!".showToast(this@UpdateAddressActivity)
                                        finish()
                                    }
                                }
                            }
                        }
                    }

                    "edit" -> {
                        val addrId = intent.getStringExtra("addressId")
                        if (addrId != null) {
                            addressViewModel.updateAddress(
                                Address(
                                    addressId = addrId,
                                    receiverName = name,
                                    phoneNumber = phone,
                                    fullAddress = addr,
                                    addressType = addrType,
                                    defaultAddress = isChooseDefault,
                                    latitude = lat!!,
                                    longitude = lng!!
                                ), userId
                            )

                            lifecycleScope.launch {
                                addressViewModel.updateAddrState.collect { res ->
                                    when (res) {
                                        is TaskResult.Loading -> {}
                                        is TaskResult.Error -> {
                                            res.exception.message?.showToast(this@UpdateAddressActivity)
                                        }

                                        is TaskResult.Success -> {
                                            "Cập nhật địa chỉ thành công!".showToast(this@UpdateAddressActivity)
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.iBtnBack.setOnClickListener {
            finish()
        }
    }
}
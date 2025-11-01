package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.AddressRepositoryImpl
import com.longpt.projectll1.data.repositoryImpl.OrderRepositoryImpl
import com.longpt.projectll1.databinding.ActivityCheckoutBinding
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.model.OrderItem
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.ChangeDefaultAddressUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.DeleteAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.domain.usecase.UpdateAddressByIdUC
import com.longpt.projectll1.presentation.adapter.CheckOutAdapter
import com.longpt.projectll1.presentation.factory.AddressViewModelFactory
import com.longpt.projectll1.presentation.factory.OrderViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AddressViewModel
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class CheckOutActivity : AppCompatActivity() {
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var orderViewModel: OrderViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid
    lateinit var binding: ActivityCheckoutBinding
    private var selectedAddrId: String? = null
    private lateinit var currentAddr: Address

    @SuppressLint("SetTextI18n")
    private val changeAddrLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == RESULT_OK) {
                val selectedId = res.data?.getStringExtra("idAddressSelected")
                if (!selectedId.isNullOrEmpty()) {
                    selectedAddrId = selectedId
                }
                addressViewModel.getAddressById(userId, selectedAddrId!!)
                lifecycleScope.launch {
                    addressViewModel.addressById.collect { res ->
                        when (res) {
                            is TaskResult.Loading -> {}
                            is TaskResult.Error -> {
                                res.exception.message?.showToast(this@CheckOutActivity)
                            }

                            is TaskResult.Success -> {
                                val addr = res.data
                                currentAddr = addr
                                binding.tvAddress.text =
                                    "${addr.receiverName} | ${addr.phoneNumber}\n${addr.fullAddress}"
                            }
                        }
                    }
                }
            }
        }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedAddrId = savedInstanceState?.getString("selectedAddrId")
        Log.d("CheckoutFlow", "onCreate - restored selectedAddrId: $selectedAddrId")

        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repoAddr = AddressRepositoryImpl(FirestoreDataSource())
        val getAddressUC = GetAddressesUC(repoAddr)
        val addAddressUC = AddAddressUC(repoAddr)
        val updateAddressByIdUC = UpdateAddressByIdUC(repoAddr)
        val deleteAddressByIdUC = DeleteAddressByIdUC(repoAddr)
        val getAddressByIdUC = GetAddressByIdUC(repoAddr)
        val addressFactory = AddressViewModelFactory(
            getAddressUC,
            addAddressUC,
            updateAddressByIdUC,
            deleteAddressByIdUC,
            getAddressByIdUC
        )
        addressViewModel = ViewModelProvider(this, addressFactory)[AddressViewModel::class.java]

        val repoOrder = OrderRepositoryImpl(FirestoreDataSource())
        val createOrderUC = CreateOrderUC(repoOrder)
        val orderFactory = OrderViewModelFactory(createOrderUC)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        if (selectedAddrId == null) {
            addressViewModel.observeAddresses(userId)
            lifecycleScope.launch {
                addressViewModel.addresses.collect { result ->
                    if (result is TaskResult.Success) {
                        val addr = result.data.firstOrNull { it.defaultAddress }
                            ?: result.data.firstOrNull()
                        if (addr != null) {
                            currentAddr = addr
                            selectedAddrId = addr.addressId
                            binding.tvAddress.text =
                                "${addr.receiverName} | ${addr.phoneNumber}\n${addr.fullAddress}"
                        } else {
                            binding.tvAddress.text = "Chưa có địa chỉ!"
                        }
                    }
                }
            }
        }

        val cartItems =
            intent.getParcelableArrayListExtra<CartItem>("orderFoodData") ?: arrayListOf()

        val totalPriceItem = cartItems.sumOf { item ->
            item.unitPrice * item.cartItemQuantity
        }
        val lastTotalPrice = totalPriceItem + 10000
        binding.tvTotalPriceItem.text = FormatUtil.moneyFormat(totalPriceItem)
        binding.tvLastTotalPrice.text = FormatUtil.moneyFormat(lastTotalPrice)
        binding.btnPlaceOrder.text = "Đặt hàng - ${FormatUtil.moneyFormat(lastTotalPrice)}"

        val adapter = CheckOutAdapter(cartItems)
        binding.rvCheckOutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckOutItems.adapter = adapter

        binding.tvChangeAddress.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            intent.putExtra("oldSelectedId", selectedAddrId)
            changeAddrLauncher.launch(intent)
        }

        binding.iBtnBack.setOnClickListener {
            finish()
        }
        binding.btnPlaceOrder.setOnClickListener {
           if(selectedAddrId == null){
               "Chưa chọn địa chỉ".showToast(this)
               return@setOnClickListener
           }else{
                val orderItems= cartItems.map {
                    OrderItem(
                        orderItemId = UUID.randomUUID().toString(),
                        orderFoodName = it.foodName,
                        orderFoodImgUrl = it.foodImgUrl,
                        orderUnitPrice = it.unitPrice,
                        orderItemQuantity = it.cartItemQuantity,
                        selectedOptions = it.selectedOptions
                    )
                }
               val totalPriceItem= totalPriceItem
               val shippingFee= 10000.0
               var paymentMethod= ""
               val orderNote= binding.edtOrderNote.text.toString()
               val orderStatus= "Pending"
               val createdAt= Timestamp.now()
               val updatedAt= Timestamp.now()


               if(binding.rbCOD.isChecked){
                   paymentMethod="COD"
               }else if(binding.rbVNPay.isChecked){
                   paymentMethod="VNPay"
               }

               val newOrder= Order("", userId,orderItems,currentAddr,totalPriceItem,paymentMethod,shippingFee,orderNote,orderStatus,createdAt,updatedAt)
               orderViewModel.createOrder(newOrder)
               lifecycleScope.launch {
                   orderViewModel.createOrderState.collect{res->
                       when(res){
                           is TaskResult.Loading -> {}
                           is TaskResult.Error -> {
                               res.exception.message?.showToast(this@CheckOutActivity)
                           }
                           is TaskResult.Success -> {
                               "Đặt hàng thành công".showToast(this@CheckOutActivity)
                               finish()
                           }
                       }
                   }
               }
           }
        }
    }
}
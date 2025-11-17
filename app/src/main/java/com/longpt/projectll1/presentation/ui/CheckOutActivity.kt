package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.google.gson.Gson
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.AddressRepositoryImpl
import com.longpt.projectll1.data.repositoryImpl.OrderRepositoryImpl
import com.longpt.projectll1.data.sharedPref.PendingOrderStorage
import com.longpt.projectll1.databinding.ActivityCheckoutBinding
import com.longpt.projectll1.domain.model.Address
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.model.OrderItem
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.CancelledOrderUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.DeleteAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.domain.usecase.GetUserOrderDetailUC
import com.longpt.projectll1.domain.usecase.GetUserOrdersByStatusUC
import com.longpt.projectll1.domain.usecase.ReOrderUC
import com.longpt.projectll1.domain.usecase.UpdateAddressByIdUC
import com.longpt.projectll1.presentation.adapter.CheckOutAdapter
import com.longpt.projectll1.presentation.factory.AddressViewModelFactory
import com.longpt.projectll1.presentation.factory.OrderViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AddressViewModel
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.VNPayUtils
import com.longpt.projectll1.utils.showToast
import com.vnpay.authentication.VNP_AuthenticationActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
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
            getAddressUC, addAddressUC, updateAddressByIdUC, deleteAddressByIdUC, getAddressByIdUC
        )
        addressViewModel = ViewModelProvider(this, addressFactory)[AddressViewModel::class.java]

        val repoOrder = OrderRepositoryImpl(FirestoreDataSource())
        val createOrderUC = CreateOrderUC(repoOrder)
        val getUserOrdersByStatusUC= GetUserOrdersByStatusUC(repoOrder)
        val getUserOrderDetailUC=GetUserOrderDetailUC(repoOrder)
        val cancelledOrderUC= CancelledOrderUC(repoOrder)
        val reOrderUC= ReOrderUC(repoOrder)
        val orderFactory = OrderViewModelFactory(createOrderUC, getUserOrdersByStatusUC, getUserOrderDetailUC,cancelledOrderUC, reOrderUC)
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

        val adapter = CheckOutAdapter(cartItems)
        binding.rvCheckOutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckOutItems.adapter = adapter

        val totalPriceItem = cartItems.sumOf { item ->
            item.unitPrice * item.cartItemQuantity
        }
        val totalPricePlusShipFee = totalPriceItem + 10000
        binding.tvTotalPriceItem.text = FormatUtil.moneyFormat(totalPriceItem)
        binding.tvLastTotalPrice.text = FormatUtil.moneyFormat(totalPricePlusShipFee)
        binding.btnPlaceOrder.text = "Đặt hàng - ${FormatUtil.moneyFormat(totalPricePlusShipFee)}"

        binding.iBtnBack.setOnClickListener {
            finish()
        }

        binding.tvChangeAddress.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            intent.putExtra("oldSelectedId", selectedAddrId)
            changeAddrLauncher.launch(intent)
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (selectedAddrId == null) {
                "Chưa chọn địa chỉ".showToast(this)
                return@setOnClickListener
            } else {
                val orderItems = cartItems.map {
                    OrderItem(
                        orderItemId = UUID.randomUUID().toString(),
                        foodId = it.foodId,
                        orderFoodName = it.foodName,
                        orderFoodImgUrl = it.foodImgUrl,
                        orderUnitPrice = it.unitPrice,
                        orderItemQuantity = it.cartItemQuantity,
                        selectedOptions = it.selectedOptions
                    )
                }
                val totalPriceItem = totalPriceItem
                val shippingFee = 10000.0
                var paymentMethod = ""
                val orderNote = binding.edtOrderNote.text.toString()
                val createdAt = Timestamp.now()
                val updatedAt = Timestamp.now()


                if (binding.rbCOD.isChecked) {
                    paymentMethod = "COD"
                } else if (binding.rbVNPay.isChecked) {
                    paymentMethod = "VNPay"
                }

                val newOrder = Order(
                    UUID.randomUUID().toString(),
                    userId,
                    orderItems,
                    currentAddr,
                    totalPriceItem,
                    paymentMethod,
                    shippingFee,
                    orderNote,
                    orderStatus= "Pending",
                    "",
                    createdAt,
                    updatedAt
                )

                if (paymentMethod == "COD") {
                    createOrder(newOrder)
                } else if (paymentMethod == "VNPay") {
                    PendingOrderStorage.saveOrder(this, newOrder)
                    openSDK(newOrder)
                }
            }
        }
    }

    private fun createOrder(newOrder: Order) {
        orderViewModel.createOrder(newOrder)
        lifecycleScope.launch {
            orderViewModel.createOrderState.collect { res ->
                when (res) {
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

    private fun openSDK(newOrder: Order) {
        val amount= newOrder.totalPrice.toLong()+ newOrder.shippingFee.toLong()
        val orderInfor= "Chuyen khoan don hang ${newOrder.orderId}"
        val paymentUrl = createVnpayUrl(amount, newOrder.orderId, orderInfor)
        val tmnCode = "VVEHDZN4"


        val intent = Intent(this, VNP_AuthenticationActivity::class.java)
        intent.putExtra("url", paymentUrl)
        intent.putExtra("tmn_code", tmnCode)
        intent.putExtra("scheme", "yummyfood")
        intent.putExtra("is_sandbox", true)

        intent.putExtra("order_data", Gson().toJson(newOrder))
        VNP_AuthenticationActivity.setSdkCompletedCallback { action ->
            when (action) {
                "AppBackAction" -> {
                 "Người dùng nhấn quay lại từ SDK".showToast(this)
                }
                "CallMobileBankingApp" -> {
                 "Người dùng chuyển sang app thanh toán (Mobile Banking / Ví)".showToast(this)
                }
                "WebBackAction" -> {
                   "Người dùng quay lại từ trang web thanh toán".showToast(this)
                }
                "FaildBackAction" -> {
                   "Thanh toán thất bại!".showToast(this)
                }
                "SuccessBackAction" -> {
                }
                else -> {
                    "Không xác định: $action".showToast(this)
                }
            }
        }
        startActivity(intent)
    }


    private fun createVnpayUrl(total: Long, orderId: String, orderInfor: String): String {
        val hashSecret = "J7W1CKVRZCQ03OE22Z1PW4O7WRN0YDKV"
        val vnpVersion = "2.1.0"
        val vnpCommand = "pay"
        val vnpTxnRef = orderId
        val vnpIpAddr = "127.0.0.1"
        val vnpTmnCode = "VVEHDZN4"
        val orderType = "order-type"
        val locate = "vn"
        val vnpCurrCode = "VND"
        val urlReturn = "yummyfood://result"

        val vnpParams = mutableMapOf<String, String>()
        vnpParams["vnp_Version"] = vnpVersion
        vnpParams["vnp_Command"] = vnpCommand
        vnpParams["vnp_TmnCode"] = vnpTmnCode
        vnpParams["vnp_Amount"] = (total * 100).toString()
        vnpParams["vnp_CurrCode"] = vnpCurrCode
        vnpParams["vnp_TxnRef"] = vnpTxnRef
        vnpParams["vnp_OrderInfo"] = orderInfor
        vnpParams["vnp_OrderType"] = orderType
        vnpParams["vnp_Locale"] = locate
        vnpParams["vnp_ReturnUrl"] = urlReturn
        vnpParams["vnp_IpAddr"] = vnpIpAddr

        val cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"))
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())

        val vnpCreateDate = formatter.format(cld.time)
        vnpParams["vnp_CreateDate"] = vnpCreateDate

        cld.add(Calendar.MINUTE, 15)
        val vnpExpireDate = formatter.format(cld.time)
        vnpParams["vnp_ExpireDate"] = vnpExpireDate

        val (hashData, query)= VNPayUtils.buildHashData(vnpParams)

        val vnpSecureHash = VNPayUtils.hmacSHA512(hashSecret, hashData.toString())
        query.append("&vnp_SecureHash=$vnpSecureHash")

        val paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?$query"
        return paymentUrl
    }
}
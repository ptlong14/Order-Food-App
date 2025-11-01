package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.CartRepositoryImpl
import com.longpt.projectll1.databinding.ActivityCartBinding
import com.longpt.projectll1.domain.usecase.AddToCartUC
import com.longpt.projectll1.domain.usecase.GetCartUC
import com.longpt.projectll1.domain.usecase.RemoveFromCartUC
import com.longpt.projectll1.domain.usecase.UpdateCartItemQuantityUC
import com.longpt.projectll1.presentation.adapter.CartAdapter
import com.longpt.projectll1.presentation.factory.CartViewModelFactory
import com.longpt.projectll1.presentation.viewModel.CartViewModel
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.showToast
import com.vnpay.authentication.VNP_AuthenticationActivity
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.TreeMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cartRepo = CartRepositoryImpl(FirestoreDataSource())
        val getCartUC = GetCartUC(cartRepo)
        val addToCartUC = AddToCartUC(cartRepo)
        val removeFromCartUC = RemoveFromCartUC(cartRepo)
        val updateCartItemQuantityUC = UpdateCartItemQuantityUC(cartRepo)
        val cartFactory =
            CartViewModelFactory(getCartUC, addToCartUC, removeFromCartUC, updateCartItemQuantityUC)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        cartViewModel.observeCart(userId)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val animator = DefaultItemAnimator()
        animator.supportsChangeAnimations = false
        binding.recyclerViewCart.itemAnimator = animator

        cartAdapter = CartAdapter(emptyList(), onClickIncrease = { cartItemId ->
            cartViewModel.increaseQuantity(cartItemId, userId)
        }, onClickDecrease = { cartItemId, currentQuantity ->
            if (currentQuantity > 1) {
                cartViewModel.decreaseQuantity(cartItemId, userId)
            } else {
                AlertDialog.Builder(this).setTitle("Xóa món khỏi giỏ hàng")
                    .setMessage("Bạn có chắc muốn xóa món này khỏi giỏ hàng không?")
                    .setPositiveButton("Xóa") { _, _ ->
                        cartViewModel.removeFromCart(cartItemId, userId)
                    }.setNegativeButton("Hủy", null).show()
            }
        })
        binding.recyclerViewCart.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewCart.adapter = cartAdapter

        lifecycleScope.launch {
            cartViewModel.cart.collect { result ->
                when (result) {
                    is TaskResult.Loading -> {
                        binding.swipeRefreshCart.isRefreshing = true
                    }

                    is TaskResult.Success -> {
                        binding.swipeRefreshCart.isRefreshing = false
                        cartAdapter.updateData(result.data)
                        if (result.data.isEmpty()) {
                            binding.recyclerViewCart.visibility = View.GONE
                            binding.ivEmptyCart.visibility = View.VISIBLE
                        } else {
                            binding.recyclerViewCart.visibility = View.VISIBLE
                            binding.ivEmptyCart.visibility = View.GONE
                        }
                        val totalPrice = result.data.sumOf { it.cartItemQuantity * it.unitPrice }
                        binding.tvTotalPrice.text = FormatUtil.moneyFormat(totalPrice)
                    }

                    is TaskResult.Error -> {
                        binding.swipeRefreshCart.isRefreshing = false
                        cartAdapter.updateData(emptyList())
                        Toast.makeText(
                            this@CartActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            cartViewModel.removeCartState.collect { result ->
                when (result) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Success -> {
                        "Đã xóa khỏi giỏ hàng".showToast(this@CartActivity)
                    }

                    is TaskResult.Error -> {
                        "Xóa thất bại: ${result.exception.message}".showToast(this@CartActivity)
                    }
                }
            }
        }
        binding.iBtnBack.setOnClickListener {
            finish()
        }
        binding.swipeRefreshCart.setOnRefreshListener {
            cartViewModel.observeCart(userId)
        }
        lifecycleScope.launch {
            cartViewModel.observeCart(userId)
            cartViewModel.cart.collect { result ->
                when (result) {
                    is TaskResult.Loading -> binding.swipeRefreshCart.isRefreshing = true
                    is TaskResult.Success -> {
                        binding.swipeRefreshCart.isRefreshing = false
                        cartAdapter.updateData(result.data)
                    }

                    is TaskResult.Error -> {
                        binding.swipeRefreshCart.isRefreshing = false
                        cartAdapter.updateData(emptyList())
                        Toast.makeText(
                            this@CartActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding.btnCheckout.setOnClickListener {
            val currentCart = cartViewModel.cart.value
            if (currentCart is TaskResult.Success && currentCart.data.isNotEmpty()) {
                val orderFoodData = currentCart.data
                val intent = Intent(this@CartActivity, CheckOutActivity::class.java)
                intent.putParcelableArrayListExtra("orderFoodData", ArrayList(orderFoodData))
                startActivity(intent)
            } else {
                "Giỏ hàng trống".showToast(this@CartActivity)
            }
        }
        binding.buttonTestVNPay.setOnClickListener {
            openSdk()
        }
    }

    private fun openSdk() {
        val paymentUrl = createVnpayUrl(1000L,"noinfor")
        Log.d("VNPay", "Payment URL: $paymentUrl")
        val tmnCode = "VVEHDZN4"


        val intent = Intent(this, VNP_AuthenticationActivity::class.java)
        intent.putExtra("url", paymentUrl)
        intent.putExtra("tmn_code", tmnCode)
        intent.putExtra("scheme", "yummyfood")
        intent.putExtra("is_sandbox", true) // Sandbox test

        VNP_AuthenticationActivity.setSdkCompletedCallback { action ->
            Log.d("VNPay", "SDK Action: $action")
            when (action) {
                "AppBackAction" -> Toast.makeText(this, "Hủy thanh toán", Toast.LENGTH_SHORT).show()
                "CallMobileBankingApp" -> Log.d("VNPay", "Chuyển sang app ngân hàng")
                "WebBackAction" -> Toast.makeText(
                    this,
                    "Đã hủy trên cổng VNPay",
                    Toast.LENGTH_SHORT
                ).show()

                "FaildBackAction" -> Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT)
                    .show()

                "SuccessBackAction" -> Toast.makeText(
                    this,
                    "Thanh toán thành công!",
                    Toast.LENGTH_SHORT
                ).show()

                else -> Log.d("VNPay", "Không xác định: $action")
            }
        }

        startActivity(intent)
    }


    private fun createVnpayUrl(total: Long, orderInfor: String): String {
        val vnpVersion = "2.1.0"
        val vnpCommand = "pay"
        val vnpTxnRef = "123"
        val vnpIpAddr = "127.0.0.1"
        val vnpTmnCode = "VVEHDZN4"
        val orderType = "order-type"
        val locate = "vn"
        val vnpCurrCode = "VND"

        var urlReturn = "yummyfood://result"

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

        // --- Build query & hashData ---
        val fieldNames = vnpParams.keys.sorted()
        val hashData = StringBuilder()
        val query = StringBuilder()

        fieldNames.forEachIndexed { index, fieldName ->
            val fieldValue = vnpParams[fieldName]
            if (!fieldValue.isNullOrEmpty()) {
                val encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())
                val encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())

                hashData.append("$encodedName=$encodedValue")
                query.append("$encodedName=$encodedValue")

                if (index < fieldNames.size - 1) {
                    hashData.append('&')
                    query.append('&')
                }
            }
        }

        val seckey= "J7W1CKVRZCQ03OE22Z1PW4O7WRN0YDKV"
        val vnpSecureHash = hmacSHA512(seckey, hashData.toString())
        query.append("&vnp_SecureHash=$vnpSecureHash")

        val paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?$query"
        return paymentUrl
    }
    
    private fun hmacSHA512(key: String, data: String): String {
        val hmac = Mac.getInstance("HmacSHA512")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA512")
        hmac.init(secretKey)
        val hashBytes = hmac.doFinal(data.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}


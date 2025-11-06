package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.SharedPef.PendingOrderStorage
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.OrderRepositoryImpl
import com.longpt.projectll1.databinding.ActivityResultBinding
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.usecase.CancelledOrderUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.GetUserOrderDetailUC
import com.longpt.projectll1.domain.usecase.GetUserOrdersByStatusUC
import com.longpt.projectll1.presentation.factory.OrderViewModelFactory
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.utils.VNPayUtils
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class CheckOutVNPayResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val hashSecret = "J7W1CKVRZCQ03OE22Z1PW4O7WRN0YDKV"
    private lateinit var orderViewModel: OrderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val repoOrder = OrderRepositoryImpl(FirestoreDataSource())
        val createOrderUC = CreateOrderUC(repoOrder)
        val getUserOrdersByStatusUC= GetUserOrdersByStatusUC(repoOrder)
        val getUserOrderDetailUC=GetUserOrderDetailUC(repoOrder)
        val cancelledOrderUC= CancelledOrderUC(repoOrder)
        val orderFactory = OrderViewModelFactory(createOrderUC, getUserOrdersByStatusUC, getUserOrderDetailUC, cancelledOrderUC)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        val uri: Uri? = intent?.data
        if (uri == null) {
            "Không nhận được phản hồi từ VNPay".showToast(this)
            finish()
            return
        }
        Log.d("CheckOutVNPayResultActivity", "URI: $uri")
        val responseCode = uri.getQueryParameter("vnp_ResponseCode")
        val isValid = verifyVNPaySignature(uri)
        if (!isValid) {
           "Sai chữ ký VNPay!".showToast(this)
            finish()
            return
        }
        if (responseCode == "00") {
            val pendingOrder = PendingOrderStorage.getOrder(this)
            if (pendingOrder == null) {
                "Không có dữ liệu đơn hàng để lưu!".showToast(this)
                finish()
                return
            }

            val order = pendingOrder
            order.orderStatus = "Pending"
            saveOrder(order)
        } else {
            if(responseCode=="24"){
                "Thanh toán thất bại: Người dùng hủy thanh toán".showToast(this)
                finish()
                return
            }
            "Thanh toán thất bại (code=$responseCode)".showToast(this)
            finish()
        }
    }

    private fun verifyVNPaySignature(uri: Uri): Boolean {
        val vnPayReturnParams = mutableMapOf<String, String>()
        for (key in uri.queryParameterNames) {
            if (key.startsWith("vnp_") && key != "vnp_SecureHash" && key != "vnp_SecureHashType") {
                uri.getQueryParameter(key)?.let {
                    vnPayReturnParams[key] = it
                }
            }
        }
        val (hashData, _) = VNPayUtils.buildHashData(vnPayReturnParams)
        val secureGetFromUri= uri.getQueryParameter("vnp_SecureHash") ?: return false
        val secureHash = VNPayUtils.hmacSHA512(hashSecret, hashData.toString())
        return secureGetFromUri == secureHash
    }
    private fun saveOrder(order: Order) {
        lifecycleScope.launch {
            orderViewModel.createOrder(order)
            orderViewModel.createOrderState.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> {
                        res.exception.message?.showToast(this@CheckOutVNPayResultActivity)
                    }

                    is TaskResult.Success -> {
                        "Thanh toán và lưu đơn hàng thành công!".showToast(this@CheckOutVNPayResultActivity)
                        PendingOrderStorage.clearOrder(this@CheckOutVNPayResultActivity)
                        val intent = Intent(this@CheckOutVNPayResultActivity, MainActivity::class.java).apply {
                            flags= Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}
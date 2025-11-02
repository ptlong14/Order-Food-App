package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.SharedPef.PendingOrderStorage
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.OrderRepositoryImpl
import com.longpt.projectll1.databinding.ActivityResultBinding
import com.longpt.projectll1.domain.model.Order
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.presentation.factory.OrderViewModelFactory
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class CheckOutVNPayResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val gson = Gson()
    private val hashSecret = "J7W1CKVRZCQ03OE22Z1PW4O7WRN0YDKV"
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid
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
        val orderFactory = OrderViewModelFactory(createOrderUC)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        val uri: Uri? = intent?.data
        if (uri == null) {
            "Không nhận được phản hồi từ VNPay".showToast(this)
            finish()
            return
        }
        val responseCode = uri.getQueryParameter("vnp_ResponseCode")

        if (responseCode == "00") {
            val pendingOrder = PendingOrderStorage.getOrder(this)
            if (pendingOrder == null) {
                "Không có dữ liệu đơn hàng để lưu!".showToast(this)
                finish()
                return
            }

            val order = pendingOrder
            order.orderStatus = "Paid"
            saveOrder(order)
        } else {
            "Thanh toán thất bại (code=$responseCode)".showToast(this)
            finish()
        }
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
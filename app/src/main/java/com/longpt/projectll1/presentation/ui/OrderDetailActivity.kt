package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.OrderRepositoryImpl
import com.longpt.projectll1.databinding.ActivityOrderDetailBinding
import com.longpt.projectll1.domain.usecase.CancelledOrderUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.GetUserOrderDetailUC
import com.longpt.projectll1.domain.usecase.GetUserOrdersByStatusUC
import com.longpt.projectll1.domain.usecase.ReOrderUC
import com.longpt.projectll1.presentation.adapter.CheckOutAdapter
import com.longpt.projectll1.presentation.adapter.OrderDetailAdapter
import com.longpt.projectll1.presentation.factory.OrderViewModelFactory
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid
    private lateinit var orderViewModel: OrderViewModel

    private var orderId: String? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val repoOrder = OrderRepositoryImpl(FirestoreDataSource())
        val createOrderUC = CreateOrderUC(repoOrder)
        val getUserOrdersByStatusUC = GetUserOrdersByStatusUC(repoOrder)
        val getUserOrderDetailUC = GetUserOrderDetailUC(repoOrder)
        val cancelledOrderUC= CancelledOrderUC(repoOrder)
        val reOrderUC= ReOrderUC(repoOrder)
        val orderFactory =
            OrderViewModelFactory(createOrderUC, getUserOrdersByStatusUC, getUserOrderDetailUC, cancelledOrderUC, reOrderUC)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        orderId = intent.getStringExtra("orderId")
        if (orderId == null) {
            finish()
            return
        }

        binding.iBtnBack.setOnClickListener { finish()}

        val adapter= OrderDetailAdapter(emptyList())
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)
        binding.rvOrderItems.adapter = adapter

        orderViewModel.getUserOrderDetail(orderId!!, userId)

        lifecycleScope.launch {
            orderViewModel.detailOrder.collect { res ->
                when(res){
                    is TaskResult.Loading -> {
                        adapter.updateData(emptyList())
                    }
                    is TaskResult.Error -> {
                        adapter.updateData(emptyList())
                        res.exception.message?.showToast(this@OrderDetailActivity)
                    }
                    is TaskResult.Success ->{
                        Log.d("OrderDetailActivity", "onCreate: ${res.data}")
                        val order =  res.data
                        binding.tvAddress.text= "${order.address.fullAddress}\n${order.address.receiverName} - ${order.address.phoneNumber}"
                        adapter.updateData(order.orderList)

                        binding.tvTotalPriceItem.text= FormatUtil.moneyFormat(order.totalPrice)
                        binding.tvLastTotalPrice.text=FormatUtil.moneyFormat(order.totalPrice+ order.shippingFee)
                        binding.tvOrderId.text=order.orderId
                        binding.tvOrderTime.text=order.createdAt.toDate().toString()
                        binding.tvPaymentMethod.text=order.paymentMethod
                        binding.edtOrderNote.text= order.orderNote.ifEmpty { "Không có ghi chú" }

                    }
                }
            }
        }
    }
}
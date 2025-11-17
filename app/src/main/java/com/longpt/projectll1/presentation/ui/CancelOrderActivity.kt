package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
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
import com.longpt.projectll1.data.repositoryImpl.OrderRepositoryImpl
import com.longpt.projectll1.databinding.ActivityCancelOrderBinding
import com.longpt.projectll1.domain.usecase.CancelledOrderUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.GetUserOrderDetailUC
import com.longpt.projectll1.domain.usecase.GetUserOrdersByStatusUC
import com.longpt.projectll1.domain.usecase.ReOrderUC
import com.longpt.projectll1.presentation.factory.OrderViewModelFactory
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class CancelOrderActivity : AppCompatActivity() {
    lateinit var binding: ActivityCancelOrderBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid
    private lateinit var orderViewModel: OrderViewModel
    private var orderId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCancelOrderBinding.inflate(layoutInflater)
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
        val cancelledOrderUC = CancelledOrderUC(repoOrder)
        val reOrderUC= ReOrderUC(repoOrder)
        val orderFactory = OrderViewModelFactory(
            createOrderUC, getUserOrdersByStatusUC, getUserOrderDetailUC, cancelledOrderUC, reOrderUC
        )
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]
        orderId = intent.getStringExtra("orderId")
        if (orderId == null) {
            finish()
            return
        }

        binding.iBtnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmitCancel.setOnClickListener {
            val selectedReason = when (binding.radioGroupReasons.checkedRadioButtonId) {
                R.id.rbReason1 -> binding.rbReason1.text.toString()
                R.id.rbReason2 -> binding.rbReason2.text.toString()
                R.id.rbReason3 -> binding.rbReason3.text.toString()
                R.id.rbReasonOther -> {
                    val otherText = binding.edtOtherReason.text.toString().trim()
                    otherText.ifEmpty { "Other" }
                }

                else -> ""
            }
            AlertDialog.Builder(this).setTitle("Xác nhận hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?")
                .setPositiveButton("Hủy đơn") { _, _ ->
                    orderViewModel.cancelOrder(orderId!!, userId, selectedReason)
                    finish()
                }.setNegativeButton("Quay lại") { _, _ ->
                    null
                }.show()
        }
        lifecycleScope.launch {
            orderViewModel.cancelOrderState.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}

                    is TaskResult.Error -> {
                        res.exception.message?.showToast(this@CancelOrderActivity)
                    }

                    is TaskResult.Success -> {
                        "Đơn hàng đã bị hủy thành công".showToast(this@CancelOrderActivity)
                    }
                }
            }
        }
    }
}
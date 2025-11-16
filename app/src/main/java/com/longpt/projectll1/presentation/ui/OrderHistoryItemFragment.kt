package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.OrderRepositoryImpl
import com.longpt.projectll1.databinding.FragmentOrderHistoryItemBinding
import com.longpt.projectll1.domain.usecase.CancelledOrderUC
import com.longpt.projectll1.domain.usecase.CreateOrderUC
import com.longpt.projectll1.domain.usecase.GetUserOrderDetailUC
import com.longpt.projectll1.domain.usecase.GetUserOrdersByStatusUC
import com.longpt.projectll1.domain.usecase.ReOrderUC
import com.longpt.projectll1.presentation.adapter.OrdersByStatusAdapter
import com.longpt.projectll1.presentation.factory.OrderViewModelFactory
import com.longpt.projectll1.presentation.viewModel.OrderViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch


class OrderHistoryItemFragment : Fragment() {
    private lateinit var binding: FragmentOrderHistoryItemBinding
    private var type: String? = null
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var ordersByStatusAdapter: OrdersByStatusAdapter
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid

    companion object {
        private const val ARG_TYPE = "ARG_TYPE"
        fun newInstance(type: String): OrderHistoryItemFragment {
            val fragment = OrderHistoryItemFragment()
            val args = Bundle()
            args.putString("ARG_TYPE", type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString(ARG_TYPE)
        val repoOrder = OrderRepositoryImpl(FirestoreDataSource())
        val createOrderUC = CreateOrderUC(repoOrder)
        val getUserOrdersByStatusUC = GetUserOrdersByStatusUC(repoOrder)
        val getUserOrderDetailUC = GetUserOrderDetailUC(repoOrder)
        val cancelledOrderUC = CancelledOrderUC(repoOrder)
        val reOrderUC = ReOrderUC(repoOrder)
        val orderFactory = OrderViewModelFactory(
            createOrderUC,
            getUserOrdersByStatusUC,
            getUserOrderDetailUC,
            cancelledOrderUC,
            reOrderUC
        )
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderHistoryItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ordersByStatusAdapter =
            OrdersByStatusAdapter(type!!, emptyList(), onClickBtn = { orderId, typeBtn ->
                when (typeBtn) {
                    "Rating" -> {
                        val fragment = BottomSheetRatingOrder.newInstance(orderId)
                        fragment.show(parentFragmentManager, "BottomSheetRatingOrder")
                    }

                    "Cancelled" -> {
                        val intent = Intent(requireContext(), CancelOrderActivity::class.java)
                        intent.putExtra("orderId", orderId)
                        startActivity(intent)
                    }

                    "Reorder" -> {
                        orderViewModel.reOrder(orderId, userId)
                        lifecycleScope.launch {
                            orderViewModel.reOrderState.collect { res ->
                                when (res) {
                                    is TaskResult.Loading -> {}
                                    is TaskResult.Error -> {
                                        res.exception.message?.showToast(requireContext())
                                    }

                                    is TaskResult.Success -> {
                                        val intent =
                                            Intent(requireContext(), CheckOutActivity::class.java)
                                        intent.putParcelableArrayListExtra(
                                            "orderFoodData", ArrayList(res.data)
                                        )
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }
            }, onClickOrderDetailBtn = { orderId ->
                val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                intent.putExtra("orderId", orderId)
                startActivity(intent)
            })
        binding.rvOrderItem.adapter = ordersByStatusAdapter
        binding.rvOrderItem.layoutManager = LinearLayoutManager(requireContext())

        type?.let {
            orderViewModel.observeOrdersByStatus(userId, it)
            lifecycleScope.launch {
                orderViewModel.ordersByStatus.collect { res ->
                    when (res) {
                        is TaskResult.Loading -> binding.swipeRefreshOrders.isRefreshing = true

                        is TaskResult.Error -> {
                            binding.swipeRefreshOrders.isRefreshing = false
                            ordersByStatusAdapter.updateData(emptyList())
                            res.exception.message?.showToast(requireContext())
                        }

                        is TaskResult.Success -> {
                            binding.swipeRefreshOrders.isRefreshing = false
                            ordersByStatusAdapter.updateData(res.data)
                            if (res.data.isEmpty()) {
                                binding.rvOrderItem.visibility = View.GONE
                                binding.layoutEmptyOrder.visibility = View.VISIBLE
                            } else {
                                binding.rvOrderItem.visibility = View.VISIBLE
                                binding.layoutEmptyOrder.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
        binding.swipeRefreshOrders.setOnRefreshListener {
            orderViewModel.observeOrdersByStatus(userId, type!!)
        }
    }
}
package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.launch

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
    }
}


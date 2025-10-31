package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.AddressRepositoryImpl
import com.longpt.projectll1.databinding.ActivityCheckoutBinding
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.ChangeDefaultAddressUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.presentation.adapter.CheckOutAdapter
import com.longpt.projectll1.presentation.factory.AddressViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AddressViewModel
import com.longpt.projectll1.utils.FormatUtil
import kotlinx.coroutines.launch

class CheckOutActivity : AppCompatActivity() {
    private lateinit var addressViewModel: AddressViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid
    lateinit var binding: ActivityCheckoutBinding
    @SuppressLint("SetTextI18n")
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
        val changeDefaultAddressUC= ChangeDefaultAddressUC(repoAddr)
        val addressFactory = AddressViewModelFactory(getAddressUC, addAddressUC, changeDefaultAddressUC)
        addressViewModel = ViewModelProvider(this, addressFactory)[AddressViewModel::class.java]

        addressViewModel.observeAddresses(userId)

        val cartItems = intent.getParcelableArrayListExtra<CartItem>("orderFoodData") ?: arrayListOf()

        val totalPriceItem = cartItems.sumOf { item ->
           item.unitPrice * item.cartItemQuantity
        }
        val lastTotalPrice= totalPriceItem + 10000
        binding.tvTotalPriceItem.text = FormatUtil.moneyFormat(totalPriceItem)
        binding.tvLastTotalPrice.text = FormatUtil.moneyFormat(lastTotalPrice)
        binding.btnPlaceOrder.text= "Đặt hàng - ${FormatUtil.moneyFormat(lastTotalPrice)}"

        val adapter = CheckOutAdapter(cartItems)
        binding.rvCheckOutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckOutItems.adapter = adapter

        binding.tvChangeAddress.setOnClickListener {
            startActivity(Intent(this, AddressActivity::class.java))
        }

        binding.iBtnBack.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            addressViewModel.defaultAddress.collect { text ->
                binding.tvAddress.text = text
            }
        }
    }
}
package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
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
import com.longpt.projectll1.data.repositoryImpl.AddressRepositoryImpl
import com.longpt.projectll1.databinding.ActivityAddressBinding
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.ChangeDefaultAddressUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.presentation.adapter.UserAddressAdapter
import com.longpt.projectll1.presentation.factory.AddressViewModelFactory
import com.longpt.projectll1.presentation.viewModel.AddressViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class AddressActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddressBinding
    private lateinit var addressViewModel: AddressViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid

    lateinit var addressAdapter: UserAddressAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val repoAddr = AddressRepositoryImpl(FirestoreDataSource())
        val getAddressUC = GetAddressesUC(repoAddr)
        val addAddressUC = AddAddressUC(repoAddr)
        val changeDefaultAddressUC = ChangeDefaultAddressUC(repoAddr)
        val addressFactory =
            AddressViewModelFactory(getAddressUC, addAddressUC, changeDefaultAddressUC)
        addressViewModel = ViewModelProvider(this, addressFactory)[AddressViewModel::class.java]

        addressViewModel.observeAddresses(userId)

        addressAdapter = UserAddressAdapter(emptyList(), onClickAddress = { addr ->
            addressViewModel.changeDefaultAddress(userId, addr.addressId)
            finish()
        })
        binding.rvSavedAddr.layoutManager = LinearLayoutManager(this)
        binding.rvSavedAddr.adapter = addressAdapter

        lifecycleScope.launch {
            addressViewModel.addresses.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> {
                        addressAdapter.updateData(emptyList())
                        res.exception.message?.showToast(this@AddressActivity)
                    }

                    is TaskResult.Success -> {
                        addressAdapter.updateData(res.data)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            addressViewModel.changeAddrState.collect { state ->
                when (state) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Success -> {
                        "Đã đặt làm mặc định!".showToast(this@AddressActivity)
                    }

                    is TaskResult.Error -> {
                        "Lỗi: ${state.exception.message}".showToast(this@AddressActivity)
                    }
                }
            }
        }

        binding.iBtnBack.setOnClickListener {
            finish()
        }

        binding.btnAddNewAddr.setOnClickListener {
            val intent = Intent(this, UpdateAddressActivity::class.java)
            startActivity(intent)
        }
    }
}
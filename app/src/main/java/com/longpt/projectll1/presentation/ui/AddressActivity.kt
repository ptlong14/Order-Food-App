package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.logger.Logger
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.AddressRepositoryImpl
import com.longpt.projectll1.databinding.ActivityAddressBinding
import com.longpt.projectll1.domain.usecase.AddAddressUC
import com.longpt.projectll1.domain.usecase.ChangeDefaultAddressUC
import com.longpt.projectll1.domain.usecase.DeleteAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressByIdUC
import com.longpt.projectll1.domain.usecase.GetAddressesUC
import com.longpt.projectll1.domain.usecase.UpdateAddressByIdUC
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
    private var oldSelectedId: String? = null

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
        val updateAddressByIdUC = UpdateAddressByIdUC(repoAddr)
        val deleteAddressByIdUC = DeleteAddressByIdUC(repoAddr)
        val getAddressByIdUC = GetAddressByIdUC(repoAddr)
        val addressFactory = AddressViewModelFactory(
            getAddressUC,
            addAddressUC,
            updateAddressByIdUC,
            deleteAddressByIdUC,
            getAddressByIdUC
        )
        addressViewModel = ViewModelProvider(this, addressFactory)[AddressViewModel::class.java]
        addressViewModel.observeAddresses(userId)

        oldSelectedId = intent.getStringExtra("oldSelectedId")
        addressAdapter = UserAddressAdapter(emptyList(), onClickAddress = { addr ->
            val resultIntent = Intent().apply {
                putExtra("idAddressSelected", addr.addressId)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }, onClickBtnEdit = { addrId ->
            val intent = Intent(this, UpdateAddressActivity::class.java)
            intent.putExtra("mode", "edit")
            intent.putExtra("addressId", addrId)
            startActivity(intent)
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

        binding.iBtnBack.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("idAddressSelected", oldSelectedId)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.btnAddNewAddr.setOnClickListener {
            val intent = Intent(this, UpdateAddressActivity::class.java)
            intent.putExtra("mode", "add")
            startActivity(intent)
        }
    }
}
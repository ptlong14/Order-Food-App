package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.remote.TypesenseDataSource
import com.longpt.projectll1.data.repositoryImpl.TypesenseSearchRepositoryImpl
import com.longpt.projectll1.databinding.ActivitySearchFoodBinding
import com.longpt.projectll1.domain.usecase.SearchFoodsUC
import com.longpt.projectll1.domain.usecase.SyncFoodsDataUC
import com.longpt.projectll1.presentation.adapter.SearchResultAdapter
import com.longpt.projectll1.presentation.factory.SearchViewModelFactory
import com.longpt.projectll1.presentation.viewModel.SearchViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFoodActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchFoodBinding

    private lateinit var searchViewModel: SearchViewModel
    private var searchJob: Job? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.searchView.isIconified = false
        binding.searchView.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        val repoSearch = TypesenseSearchRepositoryImpl(
            TypesenseDataSource(), FirestoreDataSource()
        )
        val syncFoodsDataUC = SyncFoodsDataUC(repoSearch)
        val searchFoodsUC = SearchFoodsUC(repoSearch)

        val searchFactory = SearchViewModelFactory(
            searchFoodsUC, syncFoodsDataUC

        )
        searchViewModel = ViewModelProvider(this, searchFactory)[SearchViewModel::class.java]


        val adapter = SearchResultAdapter(emptyList())
        binding.rvSearchResult.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSearchResult.adapter = adapter

//        binding.btnVoice.setOnClickListener {
//            searchViewModel.syncData()
//        }
//        lifecycleScope.launch {
//            searchViewModel.syncState.collect { res ->
//                when(res) {
//                    is TaskResult.Loading -> {}
//                    is TaskResult.Success -> {"Sync data success".showToast(this@SearchFoodActivity)}
//                    is TaskResult.Error -> {
//                        res.exception.message?.showToast(this@SearchFoodActivity)
//                    }
//                }
//            }
//        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchFood(it)
                    binding.searchView.clearFocus()
                }
                return true
            }

            @SuppressLint("SetTextI18n")
            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(200)
                    newText?.let {
                        searchViewModel.searchFood(it)
                        binding.tvTitle.text = "Kết quả tìm kiếm cho: $it"
                    }
                }
                return true
            }
        })

        lifecycleScope.launch {
            searchViewModel.searchRes.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Success -> {
                        adapter.updateData(res.data)
                    }

                    is TaskResult.Error -> {
                        res.exception.message?.showToast(this@SearchFoodActivity)
                    }
                }
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
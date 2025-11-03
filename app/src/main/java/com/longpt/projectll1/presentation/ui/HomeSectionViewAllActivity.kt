package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.FoodRepositoryImpl
import com.longpt.projectll1.databinding.ActivityHomeSectionViewAllBinding
import com.longpt.projectll1.domain.usecase.GetBestSellerUC
import com.longpt.projectll1.domain.usecase.GetNewFoodListUC
import com.longpt.projectll1.domain.usecase.GetTopRatedUC
import com.longpt.projectll1.presentation.adapter.SectionSeeAllAdapter
import com.longpt.projectll1.presentation.factory.FoodSectionSeeAllViewModelFactory
import com.longpt.projectll1.presentation.viewModel.FoodSectionSeeAllViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class HomeSectionViewAllActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeSectionViewAllBinding
    private lateinit var seeAllViewModel: FoodSectionSeeAllViewModel
    private var sectionName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeSectionViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val foodRepo = FoodRepositoryImpl(FirestoreDataSource())
        val getBestSellerUC = GetBestSellerUC(foodRepo)
        val getTopRatedUC = GetTopRatedUC(foodRepo)
        val getNewFoodListUC = GetNewFoodListUC(foodRepo)

        val factory =
            FoodSectionSeeAllViewModelFactory(getBestSellerUC, getTopRatedUC, getNewFoodListUC)

        seeAllViewModel = ViewModelProvider(this, factory)[FoodSectionSeeAllViewModel::class.java]

        binding.iBtnBack.setOnClickListener {
            finish()
        }
        val adapter = SectionSeeAllAdapter(emptyList(), onClickAdd = {foodId->
            val bts= BottomSheetFood.newInstance(foodId)
            bts.show(supportFragmentManager, "BottomSheetFood")
        })
        binding.rvSeeAll.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSeeAll.adapter = adapter

        sectionName = intent.getStringExtra("sectionName").toString()
        when (sectionName) {
            "best_seller" -> {
                binding.tvTitleSeeAll.text = "Sản phẩm bán chạy"
                seeAllViewModel.getBestSeller()
                lifecycleScope.launch {
                    seeAllViewModel.bestSeller.collect {
                        when (it) {
                            is TaskResult.Loading -> {
                                binding.swipeRefreshSeeAll.isRefreshing = true
                            }

                            is TaskResult.Success -> {
                                binding.swipeRefreshSeeAll.isRefreshing = false
                                adapter.updateData(it.data)
                            }

                            is TaskResult.Error -> {
                                it.exception.message?.showToast(this@HomeSectionViewAllActivity)
                                adapter.updateData(emptyList())
                                binding.swipeRefreshSeeAll.isRefreshing = false
                            }
                        }
                    }
                }
            }

            "top_rated" -> {
                binding.tvTitleSeeAll.text = "Sản phẩm tốt nhất"
                seeAllViewModel.getTopRated()
                lifecycleScope.launch {
                    seeAllViewModel.topRated.collect { res ->
                        when (res) {
                            is TaskResult.Loading -> {
                                binding.swipeRefreshSeeAll.isRefreshing = true
                            }

                            is TaskResult.Success -> {
                                binding.swipeRefreshSeeAll.isRefreshing = false
                                adapter.updateData(res.data)
                            }

                            is TaskResult.Error -> {
                                binding.swipeRefreshSeeAll.isRefreshing = false
                                adapter.updateData(emptyList())
                                res.exception.message?.showToast(this@HomeSectionViewAllActivity)
                            }
                        }
                    }
                }
            }

            "trending" -> {
                binding.tvTitleSeeAll.text = "Trend ẩm thực mới"
            }

            "new" -> {
                binding.tvTitleSeeAll.text = "Sản phẩm mới"
                seeAllViewModel.getNewFood()
                lifecycleScope.launch {
                    seeAllViewModel.newFood.collect { res ->
                        when (res) {
                            is TaskResult.Loading -> {
                                binding.swipeRefreshSeeAll.isRefreshing = true
                            }

                            is TaskResult.Success -> {
                                binding.swipeRefreshSeeAll.isRefreshing = false
                                adapter.updateData(res.data)
                            }

                            is TaskResult.Error -> {
                                binding.swipeRefreshSeeAll.isRefreshing = false
                                adapter.updateData(emptyList())
                                res.exception.message?.showToast(this@HomeSectionViewAllActivity)
                            }
                        }
                    }
                }
            }
        }

        binding.swipeRefreshSeeAll.setOnRefreshListener {
            when (sectionName) {
                "best_seller" -> seeAllViewModel.getBestSeller()
                "top_rated" -> seeAllViewModel.getTopRated()
                "new" -> seeAllViewModel.getNewFood()
            }
        }
    }
}
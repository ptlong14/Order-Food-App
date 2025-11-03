package com.longpt.projectll1.presentation.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.longpt.projectll1.AdminAddFood
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.BannerRepositoryImpl
import com.longpt.projectll1.data.repositoryImpl.FoodRepositoryImpl
import com.longpt.projectll1.databinding.FragmentHomeBinding
import com.longpt.projectll1.domain.usecase.GetAllBannersUC
import com.longpt.projectll1.domain.usecase.GetBestSellerUC
import com.longpt.projectll1.domain.usecase.GetNewFoodListUC
import com.longpt.projectll1.domain.usecase.GetTopRatedUC
import com.longpt.projectll1.presentation.adapter.SectionAdapter
import com.longpt.projectll1.presentation.factory.HomeViewModelFactory
import com.longpt.projectll1.presentation.modelUI.names
import com.longpt.projectll1.presentation.viewModel.HomeViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repoBanner = BannerRepositoryImpl(FirestoreDataSource())
        val repoFood = FoodRepositoryImpl(FirestoreDataSource())
        val getAllBannersUC = GetAllBannersUC(repoBanner)
        val getBestSellerUC = GetBestSellerUC(repoFood)
        val getTopRatedUC = GetTopRatedUC(repoFood)
        val getNewFoodListUC = GetNewFoodListUC(repoFood)
        val factory =
            HomeViewModelFactory(getAllBannersUC, getBestSellerUC, getTopRatedUC, getNewFoodListUC)

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.observeBanners()
        viewModel.observeHomeSections()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionsAdapter =
            SectionAdapter(sections = mutableListOf(), onSeeAllClick = { sectionType ->
                val intent = Intent(requireContext(), HomeSectionViewAllActivity::class.java)
                intent.putExtra("sectionName", sectionType.names)
                startActivity(intent)
            }, onClickCart = { food ->
                val bts = BottomSheetFood.newInstance(food.id)
                bts.show(childFragmentManager, "BottomSheetFood")
            }, onClickItem = { food ->
                val context = requireContext()
                val intent = Intent(context, DetailFoodActivity::class.java).apply {
                    putExtra("foodId", food.id)
                }
                val options = ActivityOptions.makeCustomAnimation(
                    context, R.anim.slide_in_right, R.anim.fade_out
                )
                context.startActivity(intent, options.toBundle())
            })
        binding.rvSection.adapter = sectionsAdapter
        binding.rvSection.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.banners.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> {
                        res.exception.message?.showToast(requireContext())
                    }

                    is TaskResult.Success -> {
                        val banners = res.data
                        if (banners.isNotEmpty()) {
                            val imgList = banners.map { b ->
                                SlideModel(b.imgBanner)
                            }
                            binding.imgSliderBanners.setImageList(imgList, ScaleTypes.FIT)
                            binding.imgSliderBanners.startSliding(2000)
                        } else {
                            binding.imgSliderBanners.stopSliding()
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeSections.collect { res ->
                when (res) {
                    is TaskResult.Success -> {
                        sectionsAdapter.updateData(res.data)
                        binding.swipeRefreshHome.isRefreshing = false
                    }

                    is TaskResult.Error -> {
                        res.exception.message?.showToast(requireContext())
                        sectionsAdapter.updateData(emptyList())
                        binding.swipeRefreshHome.isRefreshing = false
                    }

                    is TaskResult.Loading -> {
                        binding.swipeRefreshHome.isRefreshing = true
                    }
                }
            }
        }

        binding.swipeRefreshHome.setOnRefreshListener {
            viewModel.observeHomeSections()
        }
    }
}
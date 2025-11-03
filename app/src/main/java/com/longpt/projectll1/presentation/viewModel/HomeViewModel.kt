package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Banner
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.usecase.GetAllBannersUC
import com.longpt.projectll1.domain.usecase.GetBestSellerUC
import com.longpt.projectll1.domain.usecase.GetNewFoodListUC
import com.longpt.projectll1.domain.usecase.GetTopRatedUC
import com.longpt.projectll1.presentation.modelUI.Section
import com.longpt.projectll1.presentation.modelUI.SectionType
import com.longpt.projectll1.presentation.modelUI.description
import com.longpt.projectll1.presentation.modelUI.title
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    val getAllBannersUC: GetAllBannersUC,
    val getBestSellerFoodListUC: GetBestSellerUC,
    val getTopRatedFoodListUC: GetTopRatedUC,
    val getNewFoodListUC: GetNewFoodListUC
) : ViewModel() {
    private val _banners = MutableStateFlow<TaskResult<List<Banner>>>(TaskResult.Loading)
    val banners: StateFlow<TaskResult<List<Banner>>> = _banners

    fun observeBanners() {
        viewModelScope.launch {
            getAllBannersUC().collectLatest { result ->
                _banners.value = result
            }
        }
    }

    private val _homeSections = MutableStateFlow<TaskResult<List<Section>>>(TaskResult.Loading)
    val homeSections: StateFlow<TaskResult<List<Section>>> = _homeSections

    fun observeHomeSections() {
        viewModelScope.launch {
            _homeSections.value = TaskResult.Loading
            val jobs = listOf(
                async { getBestSellerFoodListUC() to SectionType.BEST_SELLER },
                async { getTopRatedFoodListUC() to SectionType.TOP_RATED },
                async { getNewFoodListUC() to SectionType.NEW }
            )

            val sections = jobs.awaitAll().map { (result, type) ->
                handlerTaskResult(result, type)
            }

            _homeSections.value = if (sections.isNotEmpty()) {
                TaskResult.Success(sections)
            } else {
                TaskResult.Error(Exception("Không có dữ liệu món ăn."))
            }
        }
    }

    private fun handlerTaskResult(
        result: TaskResult<List<Food>>,
        sectionType: SectionType
    ): Section {
        val title = sectionType.title
        val description = sectionType.description
        return when (result) {
            is TaskResult.Loading -> {
                Section(sectionType, title, description,emptyList(), "Loading...")
            }

            is TaskResult.Success -> {
                if (result.data.isNotEmpty()) {
                    Section(sectionType, title, description,result.data, null)
                } else {
                    Section(sectionType, title, description,emptyList(), "Không có món ăn nào")
                }
            }

            is TaskResult.Error -> {
                Section(sectionType, title, description,emptyList(), result.exception.message)
            }
        }
    }
}
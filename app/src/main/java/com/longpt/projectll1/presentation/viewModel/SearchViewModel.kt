package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.usecase.SearchFoodsUC
import com.longpt.projectll1.domain.usecase.SyncFoodsDataUC
import com.longpt.projectll1.presentation.modelUI.FoodsSearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.typesense.model.SearchResult

class SearchViewModel(
    private val searchFoodsUC: SearchFoodsUC, private val syncFoodsDataUC: SyncFoodsDataUC
) : ViewModel() {
    private val _syncState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val syncState: StateFlow<TaskResult<Unit>> = _syncState

    private val _searchRes = MutableStateFlow<TaskResult<List<FoodsSearchResult>>>(TaskResult.Loading)
    val searchRes: StateFlow<TaskResult<List<FoodsSearchResult>>> = _searchRes
    fun syncData() {
        viewModelScope.launch {
            _syncState.value = TaskResult.Loading
            val result = syncFoodsDataUC()
            _syncState.value = result
        }
    }

    fun searchFood(q: String) {
        viewModelScope.launch {
            _searchRes.value = TaskResult.Loading
            val result = searchFoodsUC(q)
            _searchRes.value = result
        }
    }
}
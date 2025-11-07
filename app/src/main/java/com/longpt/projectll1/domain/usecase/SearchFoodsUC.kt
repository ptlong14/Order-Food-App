package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.repository.TypesenseSearchRepository
import com.longpt.projectll1.presentation.modelUI.FoodsSearchResult
import org.typesense.model.SearchResult

class SearchFoodsUC(private val repository: TypesenseSearchRepository) {
    suspend operator fun invoke(query: String): TaskResult<List<FoodsSearchResult>> =
        repository.searchFood(query)
}
package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.presentation.modelUI.FoodsSearchResult

interface TypesenseSearchRepository {
    suspend fun syncFoodsDataToTypesense(): TaskResult<Unit>
    suspend fun searchFood(q: String): TaskResult<List<FoodsSearchResult>>
}
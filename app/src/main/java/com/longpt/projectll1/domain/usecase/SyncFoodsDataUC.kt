package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.repository.TypesenseSearchRepository

class SyncFoodsDataUC(private val repository: TypesenseSearchRepository) {
    suspend operator fun invoke(): TaskResult<Unit> = repository.syncFoodsDataToTypesense()
}

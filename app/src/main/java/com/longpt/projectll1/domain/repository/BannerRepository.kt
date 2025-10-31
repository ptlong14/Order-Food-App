package com.longpt.projectll1.domain.repository

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Banner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    fun getBannerList(): Flow<TaskResult<List<Banner>>>
}
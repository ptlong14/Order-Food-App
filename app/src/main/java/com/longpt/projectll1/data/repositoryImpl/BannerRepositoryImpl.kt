package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.BannerMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.domain.model.Banner
import com.longpt.projectll1.domain.repository.BannerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BannerRepositoryImpl(val dataSource: FirestoreDataSource): BannerRepository {
    override fun getBannerList(): Flow<TaskResult<List<Banner>>> {
        return dataSource.getBannerList().map {result->
            when(result){
                is TaskResult.Loading -> TaskResult.Loading
                is TaskResult.Error -> TaskResult.Error(result.exception)

                is TaskResult.Success -> {
                    val mapped = result.data.map { dto -> BannerMapper.fromDtoToDomain(dto) }
                    TaskResult.Success(mapped)
                }
            }
        }
    }
}
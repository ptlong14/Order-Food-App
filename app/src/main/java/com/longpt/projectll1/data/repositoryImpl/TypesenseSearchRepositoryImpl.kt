package com.longpt.projectll1.data.repositoryImpl

import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.FoodMapper
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.remote.TypesenseDataSource
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.repository.TypesenseSearchRepository
import com.longpt.projectll1.presentation.modelUI.FoodsSearchResult
import org.typesense.model.SearchResult

class TypesenseSearchRepositoryImpl(private val typesenseDataSource: TypesenseDataSource,
    private val firestoreDataSource: FirestoreDataSource
    ): TypesenseSearchRepository {
    override suspend fun syncFoodsDataToTypesense(): TaskResult<Unit> {

        return try{
            val foodsResult = firestoreDataSource.getAllFoodList()
            if(foodsResult is TaskResult.Error) return TaskResult.Error(foodsResult.exception)
            if(foodsResult !is TaskResult.Success) return TaskResult.Error(Exception("Foods is null"))
            val foodList = foodsResult.data

            val mapped = foodList.map { dto -> FoodMapper.fromDtoToDomain(dto) }
            val result = typesenseDataSource.syncFoods(mapped)
            if(result is TaskResult.Error) return TaskResult.Error(result.exception)
            TaskResult.Success(Unit)
        }catch (e: Exception){
            TaskResult.Error(e)
        }
    }

    override suspend fun searchFood(q: String): TaskResult<List<FoodsSearchResult>> {
        val result = typesenseDataSource.searchFood(q)
        return when(result) {
            is TaskResult.Success -> {
                try {
                    val list = result.data.hits.map { hit ->
                        FoodsSearchResult(
                            id = hit.document["id"] as String,
                            name = hit.document["name"] as String,
                            imgUrl = hit.document["imgUrl"] as String,
                            price = (hit.document["price"] as Number).toFloat()
                        )
                    }
                    TaskResult.Success(list)
                } catch (e: Exception) {
                    TaskResult.Error(e)
                }
            }
            is TaskResult.Error -> TaskResult.Error(result.exception)
            else -> TaskResult.Error(Exception("Unknown error"))
        }
    }
}
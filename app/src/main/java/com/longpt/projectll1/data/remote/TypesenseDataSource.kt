package com.longpt.projectll1.data.remote

import android.util.Log
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.typesense.api.Client
import org.typesense.api.Configuration
import org.typesense.model.CollectionSchema
import org.typesense.model.Field
import org.typesense.model.SearchParameters
import org.typesense.model.SearchResult
import org.typesense.resources.Node
import java.time.Duration


class TypesenseDataSource() {
    private var client: Client
    init {
        val node = Node("http", "192.168.1.62", "8108" )
        val config = Configuration(
            mutableListOf(node),
            Duration.ofSeconds(2),
            Duration.ofSeconds(5),
            "xyz"
        )
        client = Client(config)
    }

    suspend fun syncFoods(foods: List<Food>): TaskResult<Unit> = withContext(Dispatchers.IO) {
        try {
            try {
                client.collections("food").delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Kiểm tra collection "food"
            val existing = client.collections().retrieve().find { it.name == "food" }
            if (existing == null) {
                // Nếu chưa tồn tại thì tạo
                val schema = CollectionSchema().apply {
                    name = "food"
                    fields = listOf(
                        Field().apply { this.name = "id"; this.type = "string" },
                        Field().apply { this.name = "name"; this.type = "string"; this.index(true); this.infix(true) },
                        Field().apply { this.name = "imgUrl"; this.type = "string" },
                        Field().apply { this.name = "price"; this.type = "float" }
                    )
                    defaultSortingField = "price"
                }
                client.collections().create(schema)
            }

            // Upsert dữ liệu
            val collection = client.collections("food")
            for (food in foods) {
                val doc = mapOf(
                    "id" to food.id,
                    "name" to food.name,
                    "imgUrl" to food.imgUrl,
                    "price" to food.price
                )
                collection.documents().upsert(doc)
            }
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    suspend fun searchFood(query: String): TaskResult<SearchResult> = withContext(Dispatchers.IO) {
        try {
            val searchParams = SearchParameters().apply {
                q = query
                queryBy = "name"
            }
            val result = client.collections("food").documents().search(searchParams)
            TaskResult.Success(result)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }
}
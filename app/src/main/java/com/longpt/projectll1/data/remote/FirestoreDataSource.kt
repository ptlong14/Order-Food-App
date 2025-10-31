package com.longpt.projectll1.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.modelDTO.AddressDto
import com.longpt.projectll1.data.modelDTO.BannerDto
import com.longpt.projectll1.data.modelDTO.CartItemDto
import com.longpt.projectll1.data.modelDTO.FoodDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    //lấy ds đồ ăn theo danh mục
    suspend fun getFoodListByCategory(category: String): TaskResult<List<FoodDto>> {
        return try {
            val snapshot =
                firestore.collection("foods").whereEqualTo("category", category).get().await()
            val data = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodDto::class.java)?.copy(id = doc.id)
            }
            TaskResult.Success(data)
        } catch (e: Exception) {
            TaskResult.Error(e)

        }
    }

    //lấy ds đồ ăn theo id
    suspend fun getFoodById(foodId: String): TaskResult<FoodDto> {
        return try {
            val snapshot = firestore.collection("foods").document(foodId).get().await()
            val data = snapshot.toObject(FoodDto::class.java)
            if (data != null) {
                TaskResult.Success(data.copy(id = snapshot.id))
            } else {
                TaskResult.Error(Exception("Không có dữ liệu món ăn."))
            }

        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //thêm đồ ăn vào yêu thích
    suspend fun addToFavorite(foodDto: FoodDto, userId: String): TaskResult<Unit> {
        return try {
            val dtFood = hashMapOf(
                "name" to foodDto.name, "imgUrl" to foodDto.imgUrl, "category" to foodDto.category
            )
            firestore.collection("users").document(userId).collection("favorites")
                .document(foodDto.id).set(dtFood).await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(Exception("Có lỗi khi thêm vào yêu thích"))
        }
    }

    //xóa đồ ăn khỏi yêu thích
    suspend fun removeFromFavorite(foodId: String, userId: String): TaskResult<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("favorites").document(foodId)
                .delete().await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(Exception("Có lỗi khi xóa khỏi yêu thích"))
        }
    }

    //kiểm tra đồ ăn có trong yêu thích hay không
    fun checkFavorite(foodId: String, userId: String): Flow<TaskResult<Boolean>> = callbackFlow {
        trySend(TaskResult.Loading)
        val listener =
            firestore.collection("users").document(userId).collection("favorites").document(foodId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(TaskResult.Error(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val data = snapshot.exists()
                        trySend(TaskResult.Success(data))
                    } else {
                        trySend(TaskResult.Success(false))
                    }
                }
        awaitClose {
            listener.remove()
        }
    }

    //lấy ds đồ ăn yêu thích
    fun getFavoriteList(userId: String): Flow<TaskResult<List<FoodDto>>> = callbackFlow {
        trySend(TaskResult.Loading)
        val listener = firestore.collection("users").document(userId).collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(TaskResult.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val data = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(FoodDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(TaskResult.Success(data))
                } else {
                    trySend(TaskResult.Success(emptyList()))
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    //lấy ds banner
    fun getBannerList(): Flow<TaskResult<List<BannerDto>>> = callbackFlow {
        trySend(TaskResult.Loading)
        val listener = firestore.collection("banners").addSnapshotListener { snapshot, error ->

            if (error != null) {
                trySend(TaskResult.Error(error))
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val data = snapshot.documents.mapNotNull {
                    it.toObject(BannerDto::class.java)
                }
                trySend(TaskResult.Success(data))
            } else {
                trySend(TaskResult.Success(emptyList()))
            }
        }
        awaitClose {
            listener.remove()
        }
    }

    //lấy ds đồ ăn bán chạy nhất
    suspend fun getBestSellerFoodList(): TaskResult<List<FoodDto>> {
        return try {
            val snapshot = firestore.collection("foods")
                .orderBy("sold", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
                .await()
            val data = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodDto::class.java)?.copy(id = doc.id)
            }
            TaskResult.Success(data)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //lấy ds đồ ăn được đánh giá cao
    suspend fun getTopRatedFoodList(): TaskResult<List<FoodDto>> {
        return try {
            val snapshot = firestore.collection("foods")
                .orderBy("rating", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
                .await()
            val data = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodDto::class.java)?.copy(id = doc.id)
            }
            TaskResult.Success(data)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //lấy ds đồ ăn mới nhất
    suspend fun get8NewFoodList(): TaskResult<List<FoodDto>> {
        return try {
            val snapshot = firestore.collection("foods")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(8).get().await()
            val data = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FoodDto::class.java)?.copy(id = doc.id)
            }
            TaskResult.Success(data)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //thêm đồ ăn vào giỏ hàng
    suspend fun addToCart(cartItemDto: CartItemDto, userId: String): TaskResult<Unit> {
        return try {
            val cartItemRef = firestore.collection("users").document(userId).collection("cart")
                .document(cartItemDto.cartItemId)

            val snapshot = cartItemRef.get().await()
            if (snapshot.exists()) {
                val currentQuantity = snapshot.get("cartItemQuantity") as Long
                val newQty = currentQuantity + cartItemDto.cartItemQuantity
                cartItemRef.update("cartItemQuantity", newQty).await()
            } else {
                cartItemRef.set(cartItemDto).await()
            }
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //xóa đồ ăn khỏi giỏ hàng
    suspend fun removeFromCart(cartItemId: String, userId: String): TaskResult<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("cart").document(cartItemId)
                .delete().await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //lấy ds đồ ăn trong giỏ hàng của user
    fun getCartList(userId: String): Flow<TaskResult<List<CartItemDto>>> = callbackFlow {
        trySend(TaskResult.Loading)
        val listener = firestore.collection("users").document(userId).collection("cart")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(TaskResult.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val data = snapshot.documents.mapNotNull {
                        it.toObject(CartItemDto::class.java)?.copy(cartItemId = it.id)
                    }
                    trySend(TaskResult.Success(data))
                } else {
                    trySend(TaskResult.Success(emptyList()))
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    //cập nhật số lượng đồ ăn trong giỏ hàng
    suspend fun updateCartItemQuantity(
        cartItemId: String, newQuantity: Int, userId: String
    ): TaskResult<Unit> {
        return try {
            val cartItemRef = firestore.collection("users").document(userId).collection("cart")
                .document(cartItemId)
            val snapshot = cartItemRef.get().await()
            if (!snapshot.exists()) {
                return TaskResult.Error(Exception("Không tìm thấy mục trong giỏ hàng"))
            }
            cartItemRef.update("cartItemQuantity", newQuantity).await()
            TaskResult.Success(Unit)

        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    fun getAddresses(userId: String): Flow<TaskResult<List<AddressDto>>> = callbackFlow {
        trySend(TaskResult.Loading)
        val listener = firestore.collection("users").document(userId).collection("address")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(TaskResult.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val data = snapshot.documents.mapNotNull { addr ->
                        addr.toObject(AddressDto::class.java)?.copy(addressId = addr.id)
                    }
                    trySend(TaskResult.Success(data))
                } else {
                    trySend(TaskResult.Success(emptyList()))
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    suspend fun addAddress(address: AddressDto, userId: String): TaskResult<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("address")
                .document(address.addressId).set(address).await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(Exception("Có lỗi khi thêm địa chỉ: ${e.message}"))
        }
    }

    suspend fun changeAddress(addressId: String, userId: String): TaskResult<Unit>{
        return try {
            val batch= firestore.batch()
            val addrRef= firestore.collection("users")
                .document(userId)
                .collection("address")

            val querySnapshot= addrRef.get().await()
            if (querySnapshot.isEmpty) {
                return TaskResult.Error(Exception("Không tìm thấy địa chỉ"))
            }

            querySnapshot.documents.forEach { documentSnapshot ->
                batch.update(documentSnapshot.reference, "defaultAddress", false)
            }

            val newDefaultRef= addrRef.document(addressId)
            batch.update(newDefaultRef, "defaultAddress", true)
            batch.commit().await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }
}

package com.longpt.projectll1.data.remote

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.mapper.OrderMapper
import com.longpt.projectll1.data.modelDTO.AddressDto
import com.longpt.projectll1.data.modelDTO.BannerDto
import com.longpt.projectll1.data.modelDTO.CartItemDto
import com.longpt.projectll1.data.modelDTO.FoodDto
import com.longpt.projectll1.data.modelDTO.OrderDto
import com.longpt.projectll1.data.modelDTO.UserDto
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
            if (data.isEmpty()) {
                return TaskResult.Error(Exception("Không có món ăn nào trong danh mục này"))
            }
            TaskResult.Success(data)
        } catch (e: Exception) {
            TaskResult.Error(e)

        }
    }

    //lấy toàn bộ ds món ăn
    suspend fun getAllFoodList(): TaskResult<List<FoodDto>> {
        return try {
            val snapshot = firestore.collection("foods").get().await()

            val data = snapshot.documents.mapNotNull {
                it.toObject(FoodDto::class.java)?.copy(id = it.id)
            }
            if (data.isEmpty()) {
                return TaskResult.Error(Exception("Không có món ăn nào"))
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
            TaskResult.Error(Exception("Có lỗi khi thêm vào yêu thích:" + e.message))
        }
    }

    //xóa đồ ăn khỏi yêu thích
    suspend fun removeFromFavorite(foodId: String, userId: String): TaskResult<Unit> {
        return try {
            firestore.collection("users").document(userId).collection("favorites").document(foodId)
                .delete().await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(Exception("Có lỗi khi xóa khỏi yêu thích: " + e.message))
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
            if (data.isEmpty()) {
                return TaskResult.Error(Exception("Không có món ăn nào trong danh mục bán chạy"))
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
            if (data.isEmpty()) {
                return TaskResult.Error(Exception("Không có món ăn nào trong danh mục được đánh giá cao"))
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
            if (data.isEmpty()) {
                return TaskResult.Error(Exception("Không có món ăn nào trong danh mục mới nhất"))
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

    //xóa cart của user
    suspend fun clearCart(userId: String): TaskResult<Unit> {
        return try {
            val cartSubRef = firestore.collection("users").document(userId).collection("cart")
            val snapshot = cartSubRef.get().await()
            val batch = firestore.batch()
            snapshot.documents.forEach {
                batch.delete(it.reference)
            }
            batch.commit().await()
            TaskResult.Success(Unit)

        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //lấy ds địa chỉ của user
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

    //thêm địa chỉ cho user
    suspend fun addAddress(address: AddressDto, userId: String): TaskResult<Unit> {
        return try {
            val addressSnapshot =
                firestore.collection("users").document(userId).collection("address").get().await()

            if (addressSnapshot.isEmpty && !address.defaultAddress) {
                return TaskResult.Error(Exception("Địa chỉ đầu tiên phải được chọn làm mặc định"))
            }
            val docRef =
                firestore.collection("users").document(userId).collection("address").document()
            address.addressId = docRef.id
            docRef.set(address).await()
            if (address.defaultAddress) {
                when (val result = changeAddress(address.addressId, userId)) {
                    is TaskResult.Error -> return result
                    else -> {}
                }
            }
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            Log.d("ADD_ADDR_ERR", "addAddress: ${e.message}")
            TaskResult.Error(Exception("Có lỗi khi thêm địa chỉ: ${e.message}"))
        }
    }

    //sửa địa chỉ
    suspend fun updateAddress(updateAddress: AddressDto, userId: String): TaskResult<Unit> {
        return try {
            val addressId = updateAddress.addressId
            if (addressId.isBlank()) return TaskResult.Error(Exception("addressId trống"))

            firestore.collection("users").document(userId).collection("address").document(addressId)
                .set(updateAddress).await()

            if (updateAddress.defaultAddress) {
                when (val result = changeAddress(addressId, userId)) {
                    is TaskResult.Error -> return result
                    else -> {}
                }
            }
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //xóa địa chỉ
    suspend fun deleteAddress(addressId: String, userId: String): TaskResult<Unit> {
        return try {
            val addressDocRef = firestore.collection("users").document(userId).collection("address")
                .document(addressId)
            val addrSnapshot = addressDocRef.get().await()
            val data = addrSnapshot.toObject(AddressDto::class.java)
            if (data == null) return TaskResult.Error(Exception("Không tìm thấy địa chỉ"))
            if (data.defaultAddress) {
                return TaskResult.Error(Exception("Không thể xóa địa chỉ mặc định"))
            }
            addressDocRef.delete().await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //đổi địa chỉ
    suspend fun changeAddress(addressId: String, userId: String): TaskResult<Unit> {
        return try {
            val batch = firestore.batch()
            val addrRef = firestore.collection("users").document(userId).collection("address")

            val querySnapshot = addrRef.get().await()
            if (querySnapshot.isEmpty) {
                return TaskResult.Error(Exception("Không tìm thấy địa chỉ"))
            }

            querySnapshot.documents.forEach { documentSnapshot ->
                batch.update(documentSnapshot.reference, "defaultAddress", false)
            }

            val newDefaultRef = addrRef.document(addressId)
            batch.update(newDefaultRef, "defaultAddress", true)
            batch.commit().await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    // lấy địa chỉ theo id
    suspend fun getAddressById(addressId: String, userId: String): TaskResult<AddressDto> {
        return try {
            val snapshot = firestore.collection("users").document(userId).collection("address")
                .document(addressId).get().await()
            val data = snapshot.toObject(AddressDto::class.java)

            if (data != null) {
                TaskResult.Success(data)
            } else {
                TaskResult.Error(Exception("Địa chỉ đã bị xóa hoặc không khả dụng. Đã chuyển sang địa chỉ mặc định."))
            }
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    // tạo đơn hàng
    suspend fun createOrder(order: OrderDto): TaskResult<Unit> {
        return try {
            val orderId = order.orderId
            firestore.collection("orders").document(orderId).set(order).await()

            when (val result = clearCart(order.userId)) {
                is TaskResult.Error -> return result
                else -> {}
            }

            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //lấy danh sách đơn hàng của user theo trạng thái đơn hàng
    fun getUserOrdersByStatus(userId: String, status: String): Flow<TaskResult<List<OrderDto>>> =
        callbackFlow {
            trySend(TaskResult.Loading)
            val listener = firestore.collection("orders").whereEqualTo("userId", userId)
                .whereEqualTo("orderStatus", status).addSnapshotListener { snapshot, err ->
                    if (err != null) {
                        trySend(TaskResult.Error(err))
                        return@addSnapshotListener
                    }
                    if (snapshot == null) {
                        trySend(TaskResult.Success(emptyList()))
                    } else {
                        val data = snapshot.documents.mapNotNull {
                            it.toObject(OrderDto::class.java)
                        }
                        trySend(TaskResult.Success(data))
                    }
                }
            awaitClose {
                listener.remove()
            }
        }

    //lấy chi tiết đơn hàng theo id
    suspend fun getUserOrderDetail(orderId: String, userId: String): TaskResult<OrderDto> {
        return try {
            val snapshot = firestore.collection("orders").document(orderId).get().await()

            val data = snapshot.toObject(OrderDto::class.java)
            if (data != null && data.userId == userId) {
                TaskResult.Success(data)
            } else {
                TaskResult.Error(Exception("Không tìm thấy đơn hàng hoặc đơn này không thuộc về bạn."))
            }
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //cập nhật trạng thái đơn hàng
//    suspend fun updateOrderStatus(orderId: String, newStatus: String): TaskResult<Unit> {
//        return try {
//            val orderRef = firestore.collection("orders").document(orderId)
//
//            val snapshot = orderRef.get().await()
//            if (!snapshot.exists()) {
//                return TaskResult.Error(Exception("Không tìm thấy đơn hàng"))
//            }
//            orderRef.update(
//                mapOf(
//                    "orderStatus" to newStatus,
//                    "updatedAt" to Timestamp.now()
//                )
//            ).await()
//
//            TaskResult.Success(Unit)
//        } catch (e: Exception) {
//            TaskResult.Error(e)
//        }
//    }
    //hủy đơn hàng
    suspend fun canceledOrder(
        orderId: String, userId: String, cancelReason: String
    ): TaskResult<Unit> {
        return try {
            val orderRef = firestore.collection("orders").document(orderId)
            val snapshot = orderRef.get().await()
            if (!snapshot.exists()) {
                return TaskResult.Error(Exception("Không tìm thấy đơn hàng"))
            }
            if (snapshot.get("userId") != userId) {
                return TaskResult.Error(Exception("Đơn hàng này không thuộc về bạn"))
            }

            orderRef.update(
                mapOf(
                    "orderStatus" to "Cancelled",
                    "cancelReason" to cancelReason,
                    "updatedAt" to Timestamp.now()
                )
            )
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //mua lại đơn hàng
    suspend fun reOrder(orderId: String, userId: String): TaskResult<List<CartItemDto>> {
        return try {
            val snapshot = firestore.collection("orders").document(orderId).get().await()
            val order = snapshot.toObject(OrderDto::class.java)
            if (order == null) {
                return TaskResult.Error(Exception("Không tìm thấy đơn hàng"))
            }
            if (order.userId != userId) {
                return TaskResult.Error(Exception("Đơn hàng này không thuộc về bạn"))
            }
            val orderItems = order.orderList
            val cartItemMapped = OrderMapper.toCartItemDto(orderItems)
            TaskResult.Success(cartItemMapped)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    //lấy thông tin user
    fun getUserInfo(userId: String): Flow<TaskResult<UserDto>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(TaskResult.Error(Exception("UserId không hợp lệ")))
            close()
            return@callbackFlow
        }
        trySend(TaskResult.Loading)
        val listener =
            firestore.collection("users").document(userId).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(TaskResult.Error(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val data = snapshot.toObject(UserDto::class.java)

                    if (data != null) trySend(TaskResult.Success(data))
                    else trySend(TaskResult.Error(Exception("Thông tin người dùng null")))
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    //cập nhật thông tin người dùng
    suspend fun updateUserInfor(userId: String, field: String, value: String): TaskResult<Unit> {
        if (userId.isEmpty()) {
            return TaskResult.Error(Exception("UserId không hợp lệ"))
        }
        return try {
            firestore.collection("users").document(userId).update(field, value).await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }
    //cập nhật avatar
    suspend fun updateAvatar(userId: String, url: String): TaskResult<Unit>{
        if (userId.isEmpty()) {
            return TaskResult.Error(Exception("UserId không hợp lệ"))
        }
        return try{
            firestore.collection("users").document(userId).update("avatarUrl", url).await()
            TaskResult.Success(Unit)
        }catch (e: Exception){
            TaskResult.Error(e)
        }
    }
}

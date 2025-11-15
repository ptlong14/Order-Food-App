package com.longpt.projectll1.data.remote

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.modelDTO.UserDto
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val auth: FirebaseAuth = Firebase.auth,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun register(email: String, password: String): TaskResult<Unit> {
        return try {
            val authRes = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authRes.user?.uid ?: return TaskResult.Error(Exception("UID không hợp lệ"))

            val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            val random8 = (1..8).map { chars.random() }.joinToString("")
            val username = "user_$random8"
            val userData = hashMapOf(
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp(),
                "name" to username,
                "avatarUrl" to "",
                "bio" to ""
            )
            firestore.collection("users").document(uid).set(userData).await()
            TaskResult.Success(Unit)
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }

    suspend fun login(email: String, password: String): TaskResult<UserDto> {
        return try {
            val authRes = auth.signInWithEmailAndPassword(email, password).await()
            val uid =
                authRes.user?.uid ?: return TaskResult.Error(Exception("Lỗi khi lấy uid đăng nhập"))

            val snapshot = firestore.collection("users").document(uid).get().await()
            val data = snapshot.toObject(UserDto::class.java)
                ?: return TaskResult.Error(Exception("Lỗi khi lấy thông tin người dùng"))
            TaskResult.Success(data)
        } catch (_: FirebaseAuthInvalidUserException) {
            TaskResult.Error(Exception("Email chưa đăng ký"))
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            TaskResult.Error(Exception("Sai mật khẩu"))
        } catch (e: Exception) {
            TaskResult.Error(e)
        }
    }
}
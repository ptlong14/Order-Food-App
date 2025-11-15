package com.longpt.projectll1.data.remote

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.longpt.projectll1.core.TaskResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class CloudinaryService() {

    fun upload(uri: Uri): Flow<TaskResult<String>> = callbackFlow {
        trySend(TaskResult.Loading)

        MediaManager.get().upload(uri).unsigned("yummy_avatar_unsigned")
            .option("folder", "yummy_img_avatar").callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<Any?, Any?>?) {
                    val url = resultData?.get("secure_url") as? String
                    if (url != null) {
                        trySend(TaskResult.Success(url))
                    } else {
                        trySend(TaskResult.Error(Exception("Upload thất bại: không có URL")))
                    }
                }

                override fun onError(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                    trySend(TaskResult.Error(Exception(error?.description ?: "Lỗi upload")))
                }

                override fun onReschedule(
                    requestId: String?,
                    error: ErrorInfo?
                ) {
                }
            }).dispatch()

        awaitClose {
        }
    }
}
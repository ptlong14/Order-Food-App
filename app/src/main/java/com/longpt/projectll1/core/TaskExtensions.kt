package com.longpt.projectll1.core

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.awaitTask(): T {
    return suspendCancellableCoroutine { cont->
        addOnSuccessListener { result-> cont.resume(result) }
        addOnFailureListener { e-> cont.resumeWithException(e) }
        addOnCanceledListener { cont.resumeWithException(Exception("Task was cancelled")) }
    }
}
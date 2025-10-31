package com.longpt.projectll1.core

sealed class TaskResult<out T> {
    object Loading : TaskResult<Nothing>()
    data class Error(val exception: Exception) : TaskResult<Nothing>()
    data class Success<out T>(val data: T) : TaskResult<T>()
}
package com.longpt.projectll1.presentation.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.User
import com.longpt.projectll1.domain.usecase.GetUserInfoUC
import com.longpt.projectll1.domain.usecase.UpdateAvatarUser
import com.longpt.projectll1.domain.usecase.UpdateUserInforUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val getUserInfoUC: GetUserInfoUC,
    private val updateUserInfoUC: UpdateUserInforUC,
    private val updateAvatarUser: UpdateAvatarUser
) : ViewModel() {
    private val _userInfor = MutableStateFlow<TaskResult<User>>(TaskResult.Loading)
    val userInfor: StateFlow<TaskResult<User>> = _userInfor

    private val _updateInforState = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val updateInforState: StateFlow<TaskResult<Unit>> = _updateInforState


    private val _updateAvatarState = MutableStateFlow<TaskResult<Unit>?>(null)
    val updateAvatarState: StateFlow<TaskResult<Unit>?> = _updateAvatarState
    fun observerUserInfos(userId: String) {
        viewModelScope.launch {
            getUserInfoUC(userId).collect { result ->
                _userInfor.value = result
            }
        }
    }

    fun observerUpdateAvatar(userId: String, uri: Uri) {
        viewModelScope.launch {
            updateAvatarUser(userId, uri).collect { result ->
                _updateAvatarState.value = result
            }
        }
    }

    fun updateUserInfos(userId: String, field: String, value: String) {
        viewModelScope.launch {
            _updateInforState.value = TaskResult.Loading
            val result = updateUserInfoUC(userId, field, value)
            _updateInforState.value = result
        }
    }

}
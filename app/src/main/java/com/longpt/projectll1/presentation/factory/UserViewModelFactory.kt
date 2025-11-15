package com.longpt.projectll1.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.longpt.projectll1.domain.usecase.GetUserInfoUC
import com.longpt.projectll1.domain.usecase.UpdateAvatarUser
import com.longpt.projectll1.domain.usecase.UpdateUserInforUC
import com.longpt.projectll1.presentation.viewModel.UserViewModel

class UserViewModelFactory(
    private val getUserInfoUC: GetUserInfoUC,
    private val updateUserInfoUC: UpdateUserInforUC,
    private val updateAvatarUser: UpdateAvatarUser
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(getUserInfoUC, updateUserInfoUC, updateAvatarUser) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
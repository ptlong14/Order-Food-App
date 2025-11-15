package com.longpt.projectll1.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.domain.model.User
import com.longpt.projectll1.domain.usecase.LoginUC
import com.longpt.projectll1.domain.usecase.RegisterUC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUC: LoginUC, private val registerUC: RegisterUC
) : ViewModel() {
    private val _loginResult = MutableStateFlow<TaskResult<User>>(TaskResult.Loading)
    val loginResult: StateFlow<TaskResult<User>> = _loginResult

    private val _registerResult = MutableStateFlow<TaskResult<Unit>>(TaskResult.Loading)
    val registerResult: StateFlow<TaskResult<Unit>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginResult.value = TaskResult.Loading
            val result = loginUC(email, password)
            _loginResult.value = result
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _registerResult.value = TaskResult.Loading
            val result = registerUC(email, password)
            _registerResult.value = result
        }
    }
}
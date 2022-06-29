package com.example.heartratemonitoringapp.app.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.heartratemonitoringapp.app.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LoginViewModel(private val useCase: IUseCase) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.First)
    val loginState: StateFlow<AuthState> get() = _loginState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            useCase.login(email, password).collect {
                when (it) {
                    is Resource.Loading -> _loginState.value = AuthState.Loading
                    is Resource.Success -> _loginState.value = AuthState.Success
                    is Resource.Error -> _loginState.value =
                        AuthState.Fail(it.message ?: "Unknown Error")
                }
            }
        }
    }

    private val _validateEmail = MutableStateFlow<Boolean?>(null)
    val validateEmail: StateFlow<Boolean?> = _validateEmail
    fun setEmailValue(email: String?) {
        if (email.isNullOrBlank()) {
            _validateEmail.value = true
        } else {
            _validateEmail.value = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    private val _validatePassword = MutableStateFlow<Boolean?>(null)
    val validatePassword: StateFlow<Boolean?> = _validatePassword
    fun setPasswordValue(password: String?) {
        if (password.isNullOrBlank()) {
            _validatePassword.value = true
        } else {
            _validatePassword.value = password.length < 8
        }
    }

    val validateFieldStream
        get() = validateEmail.combine(validatePassword) { email, password ->
            if (email != null && password != null) {
                !email && !password
            } else {
                false
            }
        }

    fun setLatestLoginDate(date: String) = useCase.setLatestLoginDate(date)
}
package com.example.heartratemonitoringapp.app.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.usecase.IUseCase
import com.example.heartratemonitoringapp.app.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RegisterViewModel(private val useCase: IUseCase) : ViewModel() {

    private val _registerState = MutableStateFlow<AuthState>(AuthState.First)
    val registerState: StateFlow<AuthState> get() = _registerState

    fun register(name:String, email: String, password: String) {
        viewModelScope.launch {
            useCase.register(name, email, password).collect {
                when (it) {
                    is Resource.Loading -> _registerState.value = AuthState.Loading
                    is Resource.Success -> _registerState.value = AuthState.Success
                    is Resource.Error -> _registerState.value =
                        AuthState.Fail(it.message ?: "Unknown Error")
                }
            }
        }
    }

    private val _validateName = MutableStateFlow<Boolean?>(null)
    val validateName: StateFlow<Boolean?> = _validateName
    fun setNameValue(name: String?) {
        _validateName.value = name.isNullOrBlank()
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

    private val _validateRepassword = MutableStateFlow<Boolean?>(null)
    val validateRepassword: StateFlow<Boolean?> = _validateRepassword
    fun setRepasswordValue(password: String?, repassword: String?) {
        if (repassword.isNullOrBlank()) {
            _validateRepassword.value = true
        } else {
            _validateRepassword.value = password != repassword
        }
    }

    val validateFieldStream
        get() = validateName.combine(validateEmail) { name, email ->
            if (name != null && email != null) {
                !name && !email
            } else {
                false
            }
        }.combine(validatePassword) { first, password ->
            if (password != null) {
                !first && !password
            } else {
                false
            }
        }.combine(validateRepassword) {second, repassword ->
            if (repassword != null) {
                !second && !repassword
            } else {
                false
            }
        }
}
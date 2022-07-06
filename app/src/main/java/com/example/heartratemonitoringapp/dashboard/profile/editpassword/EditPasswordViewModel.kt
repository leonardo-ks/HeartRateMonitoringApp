package com.example.heartratemonitoringapp.dashboard.profile.editpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditPasswordViewModel(private val useCase: IUseCase) : ViewModel() {

    fun getBearer() = useCase.getBearer()

    private val _validateOldPassword = MutableStateFlow<Boolean?>(null)
    val validateOldPassword: StateFlow<Boolean?> = _validateOldPassword
    fun setOldPasswordValue(password: String?) {
        if (password.isNullOrBlank()) {
            _validateOldPassword.value = true
        } else {
            _validateOldPassword.value = password.length < 8
        }
    }

    private val _validateNewPassword = MutableStateFlow<Boolean?>(null)
    val validateNewPassword: StateFlow<Boolean?> = _validateNewPassword
    fun setNewPasswordValue(password: String?) {
        if (password.isNullOrBlank()) {
            _validateNewPassword.value = true
        } else {
            _validateNewPassword.value = password.length < 8
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
        get() = validateOldPassword.combine(validateNewPassword) { old, new ->
            if (old != null && new != null) {
                !old && !new
            } else {
                false
            }
        }.combine(validateRepassword) { first, repassword ->
            if (repassword != null) {
                first && !repassword
            } else {
                false
            }
        }

    private val _changePassword = MutableStateFlow<Resource<String>>(Resource.Loading())
    val changePassword: StateFlow<Resource<String>> get() = _changePassword
    fun changePassword(bearer: String, old: String, new: String, confirmation: String) {
        _changePassword.value = Resource.Loading()
        viewModelScope.launch {
            useCase.changePassword(bearer, old, new, confirmation).collect { res ->
                when (res) {
                    is Resource.Loading -> _changePassword.emit(Resource.Loading())
                    is Resource.Success -> _changePassword.emit(Resource.Success(res.data.toString()))
                    is Resource.Error -> _changePassword.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }
}
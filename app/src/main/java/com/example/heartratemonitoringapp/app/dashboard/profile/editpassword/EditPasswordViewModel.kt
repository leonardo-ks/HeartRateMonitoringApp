package com.example.heartratemonitoringapp.app.dashboard.profile.editpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.usecase.IUseCase
import com.example.heartratemonitoringapp.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditPasswordViewModel(private val useCase: IUseCase) : ViewModel() {

    private val _profile = MutableStateFlow<Resource<UserDataDomain>>(Resource.Loading())
    val profile: StateFlow<Resource<UserDataDomain>> get() = _profile
    fun getProfile(bearer: String) {
        _profile.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getProfile(bearer).collect { res ->
                when (res) {
                    is Resource.Loading -> _profile.emit(Resource.Loading())
                    is Resource.Success -> _profile.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _profile.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

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
                !first && !repassword
            } else {
                false
            }
        }
}
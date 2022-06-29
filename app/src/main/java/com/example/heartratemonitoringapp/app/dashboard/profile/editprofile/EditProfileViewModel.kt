package com.example.heartratemonitoringapp.app.dashboard.profile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(private val useCase: IUseCase) : ViewModel() {

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

    private val _updateProfile = MutableStateFlow<Resource<UserDataDomain>>(Resource.Loading())
    val updateProfile: StateFlow<Resource<UserDataDomain>> get() = _updateProfile
    fun updateProfile(bearer: String, name: String, email: String, dob: String, gender: Int) {
        _updateProfile.value = Resource.Loading()
        viewModelScope.launch {
            useCase.updateUser(bearer, name, email, dob, gender).collect { res ->
                when (res) {
                    is Resource.Loading -> _updateProfile.emit(Resource.Loading())
                    is Resource.Success -> _updateProfile.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _updateProfile.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    fun getBearer() = useCase.getBearer()
}
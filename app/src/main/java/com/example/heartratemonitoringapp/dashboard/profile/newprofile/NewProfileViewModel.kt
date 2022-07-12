package com.example.heartratemonitoringapp.dashboard.profile.newprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewProfileViewModel(private val useCase: IUseCase) : ViewModel() {
    private val _updateProfile = MutableStateFlow<Resource<UserDataDomain>>(Resource.Loading())
    val updateProfile: StateFlow<Resource<UserDataDomain>> get() = _updateProfile
    fun updateProfile(bearer: String, name: String, email: String, dob: String, gender: Int, height: Int, weight: Int) {
        _updateProfile.value = Resource.Loading()
        viewModelScope.launch {
            useCase.updateUser(bearer, name, email, dob, gender, height, weight).collect { res ->
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
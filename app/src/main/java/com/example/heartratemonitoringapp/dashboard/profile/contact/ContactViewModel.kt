package com.example.heartratemonitoringapp.dashboard.profile.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactViewModel(private val useCase: IUseCase) : ViewModel() {

    fun getBearer() = useCase.getBearer()

    private val _contacts = MutableStateFlow<Resource<List<UserDataDomain>>>(Resource.Loading())
    val contacts: StateFlow<Resource<List<UserDataDomain>>> get() = _contacts
    fun getContacts(bearer: String) {
        _contacts.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getContacts(bearer).collect { res ->
                when (res) {
                    is Resource.Loading -> _contacts.emit(Resource.Loading())
                    is Resource.Success -> _contacts.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _contacts.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    private val _deleteContact = MutableStateFlow<Resource<String>>(Resource.Loading())
    val deleteContact: StateFlow<Resource<String>> get() = _deleteContact
    fun deleteContact(bearer: String, contact: Int) {
        _deleteContact.value = Resource.Loading()
        viewModelScope.launch {
            useCase.deleteContact(bearer, contact).collect { res ->
                when (res) {
                    is Resource.Loading -> _deleteContact.emit(Resource.Loading())
                    is Resource.Success -> _deleteContact.emit(Resource.Success(res.data.toString()))
                    is Resource.Error -> _deleteContact.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }
}
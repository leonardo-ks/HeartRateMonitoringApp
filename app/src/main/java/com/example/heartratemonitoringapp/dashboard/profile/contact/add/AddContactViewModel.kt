package com.example.heartratemonitoringapp.dashboard.profile.contact.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddContactViewModel(private val useCase: IUseCase) : ViewModel() {

    fun getBearer() = useCase.getBearer()

    private val _addContacts = MutableStateFlow<Resource<String>>(Resource.Loading())
    val addContacts: StateFlow<Resource<String>> get() = _addContacts
    fun addContacts(bearer: String, contact: Int) {
        _addContacts.value = Resource.Loading()
        viewModelScope.launch {
            useCase.addContact(bearer, contact).collect { res ->
                when (res) {
                    is Resource.Loading -> _addContacts.emit(Resource.Loading())
                    is Resource.Success -> _addContacts.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _addContacts.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    private val _search = MutableStateFlow<Resource<List<UserDataDomain>>>(Resource.Loading())
    val search: StateFlow<Resource<List<UserDataDomain>>> get() = _search
    fun search(bearer: String, param: String) {
        _search.value = Resource.Loading()
        viewModelScope.launch {
            useCase.search(bearer, param).collect { res ->
                when (res) {
                    is Resource.Loading -> _search.emit(Resource.Loading())
                    is Resource.Success -> _search.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _search.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }
}
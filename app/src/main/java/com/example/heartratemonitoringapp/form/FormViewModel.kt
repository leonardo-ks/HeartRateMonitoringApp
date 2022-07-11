package com.example.heartratemonitoringapp.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FormViewModel(private val useCase: IUseCase): ViewModel() {
    private val _labels = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val labels: StateFlow<Resource<List<String>>> get() = _labels
    fun findData(bearer: String, avgHeartRate: Int, avgStep: Int) {
        _labels.value = Resource.Loading()
        viewModelScope.launch {
            useCase.findData(bearer, avgHeartRate, avgStep).collect {
                when (it) {
                    is Resource.Loading -> _labels.emit(Resource.Loading())
                    is Resource.Success -> _labels.emit(Resource.Success(it.data?: listOf()))
                    is Resource.Error -> _labels.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }

    private val _sendData = MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val sendData: StateFlow<Resource<Boolean>> get() = _sendData
    fun sendData(bearer: String, avgHeartRate: Int, stepChanges: Int, step: Int, label: String, createdAt: String) {
        _sendData.value = Resource.Loading()
        viewModelScope.launch {
            useCase.addData(bearer, avgHeartRate, stepChanges, step, label, createdAt).collect { res ->
                when (res) {
                    is Resource.Loading -> _sendData.emit(Resource.Loading())
                    is Resource.Success -> _sendData.emit(Resource.Success(true))
                    is Resource.Error -> _sendData.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    fun getBearer() = useCase.getBearer()
}
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
            useCase.findData(bearer, avgHeartRate, avgStep).collect { res ->
                when (res) {
                    is Resource.Loading -> _labels.emit(Resource.Loading())
                    is Resource.Success -> _labels.emit(Resource.Success(res.data?: listOf()))
                    is Resource.Error -> _labels.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    fun getBearer() = useCase.getBearer()
}
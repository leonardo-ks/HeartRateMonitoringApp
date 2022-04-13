package com.example.heartratemonitoringapp.app.dashboard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.usecase.IUseCase
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val useCase: IUseCase) : ViewModel() {

    private val _average = MutableStateFlow<Resource<AverageDomain>>(Resource.Loading())
    val average: StateFlow<Resource<AverageDomain>> get() = _average
    fun getAverage(bearer: String) {
        _average.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getAverageData(bearer).collect { res ->
                when (res) {
                    is Resource.Loading -> _average.emit(Resource.Loading())
                    is Resource.Success -> _average.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _average.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    fun getBearer() = useCase.getBearer()
}
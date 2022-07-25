package com.example.heartratemonitoringapp.dashboard.profile.contact.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.AverageDomain
import com.example.core.domain.usecase.model.MonitoringDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShowDataViewModel(private val useCase: IUseCase) : ViewModel() {

    fun getBearer() = useCase.getBearer()
    private val _average = MutableStateFlow<Resource<AverageDomain>>(Resource.Loading())
    val average: StateFlow<Resource<AverageDomain>> get() = _average
    fun getAverageById(bearer: String, id: Int) {
        _average.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getAverageDataById(bearer, id).collect { res ->
                when (res) {
                    is Resource.Loading -> _average.emit(Resource.Loading())
                    is Resource.Success -> _average.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _average.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    private val _data = MutableStateFlow<Resource<List<MonitoringDataDomain>>>(Resource.Loading())
    val data: StateFlow<Resource<List<MonitoringDataDomain>>> get() = _data
    fun getDataByDateById(bearer: String, id: Int, start: String, end: String) {
        _data.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getUserMonitoringDataByDateById(bearer, id, start, end).collect { res ->
                when (res) {
                    is Resource.Loading -> _data.emit(Resource.Loading())
                    is Resource.Success -> _data.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _data.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }
}
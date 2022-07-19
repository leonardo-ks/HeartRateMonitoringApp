package com.example.heartratemonitoringapp.dashboard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.AverageDomain
import com.example.core.domain.usecase.model.LimitDomain
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.core.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val useCase: IUseCase) : ViewModel() {

    val backgroundMonitoringState = useCase.getBackgroundMonitoringState()

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

    fun sendData(bearer: String, avgHeartRate: Int, stepChanges: Int, step: Int, label: String, createdAt: String) = useCase.addData(bearer, avgHeartRate, stepChanges, step, label, createdAt)

    fun deleteData(id: Int) = useCase.deleteMonitoringDataById(id)
    val localData = useCase.getMonitoringDataList()

    private val _data = MutableStateFlow<Resource<List<MonitoringDataDomain>>>(Resource.Loading())
    val data: StateFlow<Resource<List<MonitoringDataDomain>>> get() = _data
    fun getDataByDate(bearer: String, start: String, end: String) {
        _data.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getUserMonitoringDataByDate(bearer, start, end).collect { res ->
                when (res) {
                    is Resource.Loading -> _data.emit(Resource.Loading())
                    is Resource.Success -> _data.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _data.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    private val _sendNotification = MutableStateFlow<Resource<String>>(Resource.Loading())
    val sendNotification: StateFlow<Resource<String>> get() = _sendNotification
    fun sendNotification(bearer: String, status: Int) {
        _sendNotification.value = Resource.Loading()
        viewModelScope.launch {
            useCase.sendNotification(bearer, status).collect { res ->
                when (res) {
                    is Resource.Loading -> _sendNotification.emit(Resource.Loading())
                    is Resource.Success -> _sendNotification.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _sendNotification.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }
}
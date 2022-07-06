package com.example.heartratemonitoringapp.dashboard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.AverageDomain
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

    private val _sendData = MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val sendData: StateFlow<Resource<Boolean>> get() = _sendData
    fun sendData(bearer: String, avgHeartRate: Int, stepChanges: Int, step: Int, createdAt: String) {
        _sendData.value = Resource.Loading()
        viewModelScope.launch {
            useCase.addData(bearer, avgHeartRate, stepChanges, step, "", createdAt).collect { res ->
                when (res) {
                    is Resource.Loading -> _sendData.emit(Resource.Loading())
                    is Resource.Success -> _sendData.emit(Resource.Success(true))
                    is Resource.Error -> _sendData.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    fun deleteData(id: Int) = useCase.deleteMonitoringDataById(id)
    val localData = useCase.getMonitoringDataList()
}
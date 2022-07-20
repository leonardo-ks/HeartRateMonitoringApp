package com.example.heartratemonitoringapp.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.LimitDomain
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.core.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FormViewModel(private val useCase: IUseCase): ViewModel() {

    private val _limit = MutableStateFlow<Resource<LimitDomain>>(Resource.Loading())
    val limit: StateFlow<Resource<LimitDomain>> get() = _limit
    fun getLimit(bearer: String) {
        _sendData.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getLimit(bearer).collect { res ->
                when (res) {
                    is Resource.Loading -> _limit.emit(Resource.Loading())
                    is Resource.Success -> _limit.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _limit.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

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

    fun insertMonitoringData(data: MonitoringDataDomain) = useCase.insertMonitoringData(data)
    fun getUserId() = useCase.getUserId()
    fun deleteMonitoringDataByDate(date: String) = useCase.deleteMonitoringDataByDate(date)

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
    val anomalyDetectedTimes = useCase.getAnomalyDetectedTimes()
    fun setAnomalyDetectedTimes(times: Int) {
        useCase.setAnomalyDetectedTimes(times)
    }
    val latestAnomalyDate = useCase.getLatestAnomalyDate()
    fun setLatestAnomalyDate(date: String) = useCase.setLatestAnomalyDate(date)
}
package com.example.heartratemonitoringapp.dashboard.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _getLimit = MutableStateFlow<Resource<LimitDomain>>(Resource.Loading())
    val getLimit: StateFlow<Resource<LimitDomain>> get() = _getLimit
    fun getLimit(bearer: String, start: String, end: String) {
        _getLimit.value = Resource.Loading()
        viewModelScope.launch {
            useCase.getLimitByDate(bearer, start, end).collect { res ->
                when (res) {
                    is Resource.Loading -> _getLimit.emit(Resource.Loading())
                    is Resource.Success -> _getLimit.emit(Resource.Success(res.data!!))
                    is Resource.Error -> _getLimit.emit(Resource.Error(res.message.toString()))
                }
            }
        }
    }

    fun setMinHRLimit(min: Int) = useCase.setMinHRLimit(min)
    fun setMaxHRLimit(max: Int) = useCase.setMaxHRLimit(max)
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
    }}
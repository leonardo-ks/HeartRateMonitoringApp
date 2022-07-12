package com.example.heartratemonitoringapp.dashboard.band

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.model.MonitoringDataDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BandViewModel(private val useCase: IUseCase) : ViewModel() {
    val backgroundMonitoringState = useCase.getBackgroundMonitoringState()
    fun setBackgroundMonitoringState(state: Boolean) = useCase.setBackgroundMonitoringState(state)
    val monitoringPeriod = useCase.getMonitoringPeriod()
    fun setMonitoringPeriod(period: Int) = useCase.setMonitoringPeriod(period)
    fun getLower() = useCase.getMinHRLimit()
    fun getUpper() = useCase.getMaxHRLimit()

    fun getBearer() = useCase.getBearer()

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
}
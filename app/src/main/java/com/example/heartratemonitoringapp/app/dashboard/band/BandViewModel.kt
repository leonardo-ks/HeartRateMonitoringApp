package com.example.heartratemonitoringapp.app.dashboard.band

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.usecase.IUseCase
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Period

class BandViewModel(private val useCase: IUseCase) : ViewModel() {
    val backgroundMonitoringState = useCase.getBackgroundMonitoringState()
    fun setBackgroundMonitoringState(state: Boolean) = useCase.setBackgroundMonitoringState(state)
    val monitoringPeriod = useCase.getMonitoringPeriod()
    fun setMonitoringPeriod(period: Int) = useCase.setMonitoringPeriod(period)
}
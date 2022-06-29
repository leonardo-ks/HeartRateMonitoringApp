package com.example.heartratemonitoringapp.app.dashboard.band

import androidx.lifecycle.ViewModel
import com.example.core.domain.usecase.IUseCase

class BandViewModel(private val useCase: IUseCase) : ViewModel() {
    val backgroundMonitoringState = useCase.getBackgroundMonitoringState()
    fun setBackgroundMonitoringState(state: Boolean) = useCase.setBackgroundMonitoringState(state)
    val monitoringPeriod = useCase.getMonitoringPeriod()
    fun setMonitoringPeriod(period: Int) = useCase.setMonitoringPeriod(period)
}
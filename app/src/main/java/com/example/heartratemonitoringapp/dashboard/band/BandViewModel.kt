package com.example.heartratemonitoringapp.dashboard.band

import androidx.lifecycle.ViewModel
import com.example.core.domain.usecase.IUseCase

class BandViewModel(private val useCase: IUseCase) : ViewModel() {
    val backgroundMonitoringState = useCase.getBackgroundMonitoringState()
    fun setBackgroundMonitoringState(state: Boolean) = useCase.setBackgroundMonitoringState(state)
    val monitoringPeriod = useCase.getMonitoringPeriod()
    fun setMonitoringPeriod(period: Int) = useCase.setMonitoringPeriod(period)
    val minHRLimit = useCase.getMinHRLimit()
    fun setMinHRLimit(min: Int) = useCase.setMinHRLimit(min)
    val maxHRLimit = useCase.getMaxHRLimit()
    fun setMaxHRLimit(max: Int) = useCase.setMaxHRLimit(max)
}
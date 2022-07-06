package com.example.heartratemonitoringapp.monitoring.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.Resource
import com.example.core.domain.usecase.IUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveMonitoringViewModel(private val useCase: IUseCase): ViewModel() {
    private var heartRate: MutableLiveData<Int> = MutableLiveData()
    val heartRateValue: LiveData<Int>
        get() = heartRate
    fun updateHeartRateValue(value: Int) {
        heartRate.postValue(value)
    }

    private var step: MutableLiveData<Int> = MutableLiveData()
    val stepValue: LiveData<Int>
        get() = step
    fun updateStepValue(value: Int) {
        step.postValue(value)
    }
}
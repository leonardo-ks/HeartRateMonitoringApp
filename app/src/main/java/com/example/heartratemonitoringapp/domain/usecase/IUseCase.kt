package com.example.heartratemonitoringapp.domain.usecase

import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import com.example.heartratemonitoringapp.domain.usecase.model.LoginDomain
import com.example.heartratemonitoringapp.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.Flow

interface IUseCase {
    fun login(email: String, password: String): Flow<Resource<LoginDomain>>
    fun register(name: String, email: String, password: String): Flow<Resource<UserDataDomain>>
    fun logout(bearer: String): Flow<Resource<String>>
    fun addData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>>
    fun findData(bearer: String, avgHeartRate: Int, avgStep: Int): Flow<Resource<List<String>>>
    fun getProfile(bearer: String): Flow<Resource<UserDataDomain>>
    fun getUserMonitoringData(bearer: String): Flow<Resource<List<MonitoringDataDomain>>>
    fun getAverageData(bearer: String): Flow<Resource<AverageDomain>>
    fun deleteData(bearer: String, id: Int): Flow<Resource<String>>
    fun updateMonitoringData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>>
    fun updateUser(bearer: String, name: String): Flow<Resource<UserDataDomain>>
    fun setBearer(bearer: String)
    fun getBearer(): Flow<String?>
    fun setLoginState(state: Boolean)
    fun getLoginState(): Flow<Boolean>
}
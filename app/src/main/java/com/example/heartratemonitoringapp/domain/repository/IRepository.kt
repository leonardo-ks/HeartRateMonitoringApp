package com.example.heartratemonitoringapp.domain.repository

import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import com.example.heartratemonitoringapp.domain.usecase.model.LoginDomain
import com.example.heartratemonitoringapp.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun login(email: String, password: String): Flow<Resource<LoginDomain>>
    fun register(name: String, email: String, password: String): Flow<Resource<UserDataDomain>>
    fun logout(bearer: String): Flow<Resource<String>>
    fun addData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>>
    fun getProfile(bearer: String): Flow<Resource<UserDataDomain>>
    fun getUserMonitoringData(bearer: String): Flow<Resource<List<MonitoringDataDomain>>>
    fun getAverageData(bearer: String): Flow<Resource<AverageDomain>>
    fun deleteData(bearer: String, id: Int): Flow<Resource<String>>
    fun updateMonitoringData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>>
    fun updateUser(bearer: String, name: String): Flow<Resource<UserDataDomain>>
    fun setBearer(bearer: String)
    fun getBearer()
    fun setUserEmail(email: String)
    fun getUserEmail()
    fun setLoginState(state: Boolean)
    fun getLoginState()
}
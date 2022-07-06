package com.example.core.domain.repository

import com.example.core.data.Resource
import com.example.core.domain.usecase.model.AverageDomain
import com.example.core.domain.usecase.model.LoginDomain
import com.example.core.domain.usecase.model.MonitoringDataDomain
import com.example.core.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun login(email: String, password: String): Flow<Resource<LoginDomain>>
    fun register(name: String, email: String, password: String): Flow<Resource<UserDataDomain>>
    fun logout(bearer: String): Flow<Resource<String>>
    fun addData(bearer: String, avgHeartRate: Int, stepChanges: Int, step:Int, label: String?, createdAt: String?): Flow<Resource<Boolean>>
    fun findData(bearer: String, avgHeartRate: Int, avgStep: Int): Flow<Resource<List<String>>>
    fun getProfile(bearer: String): Flow<Resource<UserDataDomain>>
    fun getUserMonitoringData(bearer: String): Flow<Resource<List<MonitoringDataDomain>>>
    fun getAverageData(bearer: String): Flow<Resource<AverageDomain>>
    fun deleteData(bearer: String, id: Int): Flow<Resource<String>>
    fun updateMonitoringData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>>
    fun updateUser(bearer: String, name: String, email: String, dob: String, gender: Int): Flow<Resource<UserDataDomain>>
    fun changePassword(bearer: String, old: String, new: String, confirmation: String): Flow<Resource<String>>
    fun setBearer(bearer: String)
    fun getBearer(): Flow<String?>
    fun setLoginState(state: Boolean)
    fun getLoginState(): Flow<Boolean>
    fun setUserId(id: Int)
    fun getUserId(): Flow<Int>
    fun setLatestLoginDate(date: String)
    fun getLatestLoginDate(): Flow<String?>
    fun setMonitoringPeriod(period: Int)
    fun getMonitoringPeriod(): Flow<Int>
    fun setBackgroundMonitoringState(state: Boolean)
    fun getBackgroundMonitoringState(): Flow<Boolean>
    fun setMinHRLimit(min: Int)
    fun getMinHRLimit(): Flow<Int>
    fun setMaxHRLimit(max: Int)
    fun getMaxHRLimit(): Flow<Int>
    fun getMonitoringDataList(): List<MonitoringDataDomain>
    fun deleteMonitoringDataById(id: Int)
    fun deleteMonitoringDataByDate(date: String)
    fun insertMonitoringData(monitoringData: MonitoringDataDomain)
}
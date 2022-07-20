package com.example.core.domain.repository

import com.example.core.data.Resource
import com.example.core.domain.usecase.model.*
import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun login(email: String, password: String): Flow<Resource<LoginDomain>>
    fun register(name: String, email: String, password: String): Flow<Resource<UserDataDomain>>
    fun logout(bearer: String): Flow<Resource<String>>
    fun addData(bearer: String, avgHeartRate: Int, stepChanges: Int, step:Int, label: String?, createdAt: String?): Flow<Resource<Boolean>>
    fun findData(bearer: String, avgHeartRate: Int, avgStep: Int): Flow<Resource<List<String>>>
    fun getProfile(bearer: String): Flow<Resource<UserDataDomain>>
    fun getUserMonitoringData(bearer: String): Flow<Resource<List<MonitoringDataDomain>>>
    fun getUserMonitoringDataByDate(bearer: String, start: String, end: String): Flow<Resource<List<MonitoringDataDomain>>>
    fun getUserMonitoringDataByDateById(bearer: String, id:Int, start: String, end: String): Flow<Resource<List<MonitoringDataDomain>>>
    fun getAverageData(bearer: String): Flow<Resource<AverageDomain>>
    fun getAverageDataById(bearer: String, id: Int): Flow<Resource<AverageDomain>>
    fun deleteData(bearer: String, id: Int): Flow<Resource<String>>
    fun updateMonitoringData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>>
    fun updateUser(bearer: String, name: String, email: String, dob: String, gender: Int, height: Int, weight: Int): Flow<Resource<UserDataDomain>>
    fun changePassword(bearer: String, old: String, new: String, confirmation: String): Flow<Resource<String>>
    fun addContact(bearer: String, contact: Int): Flow<Resource<String>>
    fun deleteContact(bearer: String, contact: Int): Flow<Resource<String>>
    fun sendNotification(bearer: String, status: Int, vibrate: Boolean): Flow<Resource<String>>
    fun getContacts(bearer: String): Flow<Resource<List<UserDataDomain>>>
    fun getLimit(bearer: String): Flow<Resource<LimitDomain>>
    fun search(bearer: String, param: String): Flow<Resource<List<UserDataDomain>>>
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
    fun setAnomalyDetectedTimes(times: Int)
    fun getAnomalyDetectedTimes(): Flow<Int>
    fun setLatestAnomalyDate(date : String)
    fun getLatestAnomalyDate(): Flow<String?>
    fun getMonitoringDataList(): List<MonitoringDataDomain>
    fun deleteMonitoringDataById(id: Int)
    fun deleteMonitoringDataByDate(date: String)
    fun insertMonitoringData(monitoringData: MonitoringDataDomain)
}
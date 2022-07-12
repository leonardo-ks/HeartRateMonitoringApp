package com.example.core.data.source.local

import com.example.core.data.source.local.entities.MonitoringDataEntities
import com.example.core.data.source.local.room.Dao
import com.example.core.data.source.local.sharedpref.ISharedPreferences

class LocalDataSource(private val dao: Dao, private val sharedPreferences: ISharedPreferences) {
    fun setBearer(bearer: String?) = sharedPreferences.setBearer(bearer)
    fun getBearer() = sharedPreferences.getBearer()
    fun setLoginState(state: Boolean) = sharedPreferences.setLoginState(state)
    fun getLoginState() = sharedPreferences.getLoginState()
    fun setUserId(id: Int) = sharedPreferences.setUserId(id)
    fun getUserId() = sharedPreferences.getUserId()
    fun setLatestLoginDate(date: String) = sharedPreferences.setLatestLoginDate(date)
    fun getLatestLoginDate() = sharedPreferences.getLatestLoginDate()
    fun setMonitoringPeriod(period: Int) = sharedPreferences.setMonitoringPeriod(period)
    fun getMonitoringPeriod() = sharedPreferences.getMonitoringPeriod()
    fun setBackgroundMonitoringState(state: Boolean) = sharedPreferences.setBackgroundMonitoringState(state)
    fun getBackgroundMonitoringState() = sharedPreferences.getBackgroundMonitoringState()
    fun setMinHRLimit(min: Int) = sharedPreferences.setMinHRLimit(min)
    fun getMinHRLimit(): Int = sharedPreferences.getMinHRLimit()
    fun setMaxHRLimit(max: Int) = sharedPreferences.setMaxHRLimit(max)
    fun getMaxHRLimit(): Int = sharedPreferences.getMaxHRLimit()
    fun setAnomalyDetectedTimes(times: Int) = sharedPreferences.setAnomalyDetectedTimes(times)
    fun getAnomalyDetectedTimes(): Int = sharedPreferences.getAnomalyDetectedTimes()
    fun setLatestAnomalyDate(date : String) = sharedPreferences.setLatestAnomalyDate(date)
    fun getLatestAnomalyDate(): String? = sharedPreferences.getLatestAnomalyDate()

    fun getMonitoringDataList() = dao.getMonitoringDataList()
    fun deleteMonitoringDataById(id: Int) = dao.deleteMonitoringDataById(id)
    fun deleteMonitoringDataByDate(date: String) = dao.deleteMonitoringDataByDate(date)
    fun insertMonitoringData(monitoringData: MonitoringDataEntities) = dao.insertMonitoringData(monitoringData)
}
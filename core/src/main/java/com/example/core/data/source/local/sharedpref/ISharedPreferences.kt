package com.example.core.data.source.local.sharedpref

interface ISharedPreferences {
    fun setBearer(bearer : String?)
    fun getBearer() : String?
    fun setLoginState(state : Boolean)
    fun getLoginState() : Boolean
    fun setUserId(id: Int)
    fun getUserId(): Int
    fun setLatestLoginDate(date : String?)
    fun getLatestLoginDate(): String?
    fun setMonitoringPeriod(period: Int)
    fun getMonitoringPeriod(): Int
    fun setBackgroundMonitoringState(state : Boolean)
    fun getBackgroundMonitoringState() : Boolean
    fun setMinHRLimit(min: Int)
    fun getMinHRLimit(): Int
    fun setMaxHRLimit(max: Int)
    fun getMaxHRLimit(): Int
}
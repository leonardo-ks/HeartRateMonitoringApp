package com.example.heartratemonitoringapp.data.source.local

import com.example.heartratemonitoringapp.data.source.local.room.Dao
import com.example.heartratemonitoringapp.data.source.local.sharedpref.ISharedPreferences

class LocalDataSource(private val dao: Dao, private val sharedPreferences: ISharedPreferences) {
    fun setBearer(bearer: String?) = sharedPreferences.setBearer(bearer)
    fun getBearer() = sharedPreferences.getBearer()
    fun setLoginState(state: Boolean) = sharedPreferences.setLoginState(state)
    fun getLoginState() = sharedPreferences.getLoginState()
    fun setLatestLoginDate(date: String?) = sharedPreferences.setLatestLoginDate(date)
    fun getLatestLoginDate() = sharedPreferences.getLatestLoginDate()
}
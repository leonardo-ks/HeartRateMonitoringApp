package com.example.heartratemonitoringapp.data.source.local.sharedpref

interface ISharedPreferences {
    fun setBearer(bearer : String?)
    fun getBearer() : String?
    fun setLoginState(state : Boolean)
    fun getLoginState() : Boolean
    fun setLatestLoginDate(date : String?)
    fun getLatestLoginDate(): String?
}
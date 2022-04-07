package com.example.heartratemonitoringapp.data.source.local.sharedpref

interface ISharedPreferences {
    fun setBearer(bearer : String?)
    fun getBearer() : String
    fun setUserEmail(email : String?)
    fun getUserEmail() : String
    fun setLoginState(state : Boolean)
    fun getLoginState() : Boolean
}
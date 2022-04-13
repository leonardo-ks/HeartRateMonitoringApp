package com.example.heartratemonitoringapp.data.source.local.sharedpref

class SharedPreferences(private val sharedPref: SharedPref): ISharedPreferences {
    override fun setBearer(bearer: String?) = sharedPref.setString("bearer", "Bearer $bearer")
    override fun getBearer(): String = sharedPref.getString("bearer")
    override fun setLoginState(state: Boolean) = sharedPref.setBoolean("login", state)
    override fun getLoginState(): Boolean = sharedPref.getBoolean("login")
}
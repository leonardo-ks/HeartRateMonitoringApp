package com.example.heartratemonitoringapp.data.source.local.sharedpref

class SharedPreferences(private val sharedPref: SharedPref): ISharedPreferences {
    override fun setBearer(bearer: String?) = sharedPref.setString("bearer", "Bearer $bearer")
    override fun getBearer(): String = sharedPref.getString("bearer")
    override fun setLoginState(state: Boolean) = sharedPref.setBoolean("login", state)
    override fun getLoginState(): Boolean = sharedPref.getBoolean("login")
    override fun setLatestLoginDate(date : String?) = sharedPref.setString("date", date)
    override fun getLatestLoginDate(): String = sharedPref.getString("date")
    override fun setMonitoringPeriod(period: Int) = sharedPref.setInt("period", period)
    override fun getMonitoringPeriod(): Int = sharedPref.getInt("period")
    override fun setBackgroundMonitoringState(state: Boolean) = sharedPref.setBoolean("background", state)
    override fun getBackgroundMonitoringState(): Boolean = sharedPref.getBoolean("background")
}
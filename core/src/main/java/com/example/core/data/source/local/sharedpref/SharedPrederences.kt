package com.example.core.data.source.local.sharedpref

class SharedPreferences(private val sharedPref: SharedPref): ISharedPreferences {
    override fun setBearer(bearer: String?) = sharedPref.setString("bearer", "Bearer $bearer")
    override fun getBearer(): String = sharedPref.getString("bearer")
    override fun setLoginState(state: Boolean) = sharedPref.setBoolean("login", state)
    override fun getLoginState(): Boolean = sharedPref.getBoolean("login")
    override fun setUserId(id: Int) = sharedPref.setInt("id", id)
    override fun getUserId(): Int = sharedPref.getInt("id")
    override fun setLatestLoginDate(date : String) = sharedPref.setString("date", date)
    override fun getLatestLoginDate(): String = sharedPref.getString("date")
    override fun setMonitoringPeriod(period: Int) = sharedPref.setInt("period", period)
    override fun getMonitoringPeriod(): Int = sharedPref.getInt("period")
    override fun setBackgroundMonitoringState(state: Boolean) = sharedPref.setBoolean("background", state)
    override fun getBackgroundMonitoringState(): Boolean = sharedPref.getBoolean("background")
    override fun setMinHRLimit(min: Int) = sharedPref.setInt("min", min)
    override fun getMinHRLimit(): Int = sharedPref.getInt("min")
    override fun setMaxHRLimit(max: Int) = sharedPref.setInt("max", max)
    override fun getMaxHRLimit(): Int = sharedPref.getInt("max")
    override fun setAnomalyDetectedTimes(times: Int) = sharedPref.setInt("anomaly", times)
    override fun getAnomalyDetectedTimes(): Int = sharedPref.getInt("anomaly")
    override fun setLatestAnomalyDate(date : String) = sharedPref.setString("anomalyDate", date)
    override fun getLatestAnomalyDate(): String = sharedPref.getString("anomalyDate")
}
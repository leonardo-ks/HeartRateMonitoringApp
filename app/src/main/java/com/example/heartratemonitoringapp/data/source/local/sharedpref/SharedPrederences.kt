package com.example.heartratemonitoringapp.data.source.local.sharedpref

class SharedPrederences(private val sharedPref: SharedPref): ISharedPreferences {
    override fun setBearer(bearer: String?) = sharedPref.setString("bearer", bearer)

    override fun getBearer(): String = sharedPref.getString("bearer")

    override fun setUserEmail(email: String?) = sharedPref.setString("email", email)

    override fun getUserEmail(): String = sharedPref.getString("email")

    override fun setLoginState(state: Boolean) = sharedPref.setBoolean("login", state)

    override fun getLoginState(): Boolean = sharedPref.getBoolean("login")
}
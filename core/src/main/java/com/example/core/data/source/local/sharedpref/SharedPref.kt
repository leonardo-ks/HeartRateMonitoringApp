package com.example.core.data.source.local.sharedpref

import android.content.SharedPreferences

class SharedPref(private val sharedPref: SharedPreferences) {
    fun setString(key: String, value: String?) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String): String = sharedPref.getString(key, "") ?: ""

    fun setBoolean(key: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String): Boolean = sharedPref.getBoolean(key, false)

    fun setInt(key: String, value: Int) {
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    fun getInt(key: String): Int = sharedPref.getInt(key, 60000)
}
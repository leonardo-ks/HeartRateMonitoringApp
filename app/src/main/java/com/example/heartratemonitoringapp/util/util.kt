package com.example.heartratemonitoringapp.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import okhttp3.ResponseBody
import org.json.JSONObject

fun ResponseBody.getMessage(): String {
    return try {
        val jsonObj = JSONObject(this.string())
        jsonObj.getString("message")
    } catch (e: Exception) {
        e.message.toString()
    }
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
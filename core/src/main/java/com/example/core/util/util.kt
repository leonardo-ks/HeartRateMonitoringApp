package com.example.core.util

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
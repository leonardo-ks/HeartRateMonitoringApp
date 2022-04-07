package com.example.heartratemonitoringapp.util

import com.example.heartratemonitoringapp.data.source.remote.response.AverageResponse
import com.example.heartratemonitoringapp.data.source.remote.response.LoginResponse
import com.example.heartratemonitoringapp.data.source.remote.response.MonitoringData
import com.example.heartratemonitoringapp.data.source.remote.response.UserData
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import com.example.heartratemonitoringapp.domain.usecase.model.LoginDomain
import com.example.heartratemonitoringapp.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.domain.usecase.model.UserDataDomain
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

fun LoginResponse.toDomain(): LoginDomain = LoginDomain(accessToken)

fun UserData.toDomain(): UserDataDomain = UserDataDomain(profile, name, id)

fun MonitoringData.toDomain(): MonitoringDataDomain = MonitoringDataDomain(avgHeartRate, userId, createdAt, id, label, avgStep)

fun List<MonitoringData?>.toDomain() = listOf(MonitoringDataDomain())

fun AverageResponse.toDomain(): AverageDomain = AverageDomain(avgHeartRate, avgStep)
package com.example.heartratemonitoringapp.data

import com.example.heartratemonitoringapp.data.source.remote.response.AverageResponse
import com.example.heartratemonitoringapp.data.source.remote.response.LoginResponse
import com.example.heartratemonitoringapp.data.source.remote.response.MonitoringData
import com.example.heartratemonitoringapp.data.source.remote.response.UserData
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import com.example.heartratemonitoringapp.domain.usecase.model.LoginDomain
import com.example.heartratemonitoringapp.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.domain.usecase.model.UserDataDomain

fun LoginResponse.toDomain(): LoginDomain = LoginDomain(accessToken)

fun UserData.toDomain(): UserDataDomain = UserDataDomain(
    email = email,
    name = name,
    id = id,
    profile = profile
)

fun MonitoringData.toDomain(): MonitoringDataDomain = MonitoringDataDomain(
    avgHeartRate = avgHeartRate,
    userId = userId,
    createdAt = createdAt,
    id = id,
    label = label,
    avgStep = avgStep
)

fun AverageResponse.toDomain(): AverageDomain = AverageDomain(
    avgHeartRate = avgHeartRate,
    avgStep = avgStep
)
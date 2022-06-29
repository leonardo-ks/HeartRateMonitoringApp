package com.example.heartratemonitoringapp.data

import com.example.heartratemonitoringapp.data.source.local.entities.MonitoringDataEntities
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
    dob = dob,
    id = id,
    gender = gender,
    profile = profile
)

fun MonitoringData.toDomain(): MonitoringDataDomain = MonitoringDataDomain(
    avgHeartRate = avgHeartRate,
    userId = userId,
    createdAt = createdAt,
    id = id,
    label = label,
    stepChanges = stepChanges,
    step = step
)

fun AverageResponse.toDomain(): AverageDomain = AverageDomain(
    avgHeartRate = avgHeartRate,
    todaySteps = todaySteps
)

fun MonitoringDataDomain.toEntities(): MonitoringDataEntities = MonitoringDataEntities(
    id = id,
    userId = userId,
    avgHeartRate = avgHeartRate,
    stepChanges = stepChanges,
    label = label,
    createdAt = createdAt,
    step = step
)

fun MonitoringDataEntities.toDomain(): MonitoringDataDomain = MonitoringDataDomain(
    avgHeartRate = avgHeartRate,
    userId = userId,
    createdAt = createdAt,
    id = id,
    label = label,
    stepChanges = stepChanges,
    step = step
)
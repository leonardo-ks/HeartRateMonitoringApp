package com.example.core.data

import com.example.core.data.source.local.entities.MonitoringDataEntities
import com.example.core.data.source.remote.response.*
import com.example.core.domain.usecase.model.*

fun LoginResponse.toDomain(): LoginDomain = LoginDomain(accessToken)

fun UserData.toDomain(): UserDataDomain = UserDataDomain(
    email = email,
    name = name,
    dob = dob,
    id = id,
    gender = gender,
    profile = profile,
    height = height,
    weight = weight
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

fun LimitResponse.toDomain(): LimitDomain = LimitDomain(
    lower, upperStill, upperWalk
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
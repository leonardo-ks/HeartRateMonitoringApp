package com.example.heartratemonitoringapp.domain.usecase.model

data class MonitoringDataDomain(
    val avgHeartRate: Int? = null,
    val userId: Int? = null,
    val createdAt: String? = null,
    val id: Int? = null,
    val label: String? = null,
    val stepChanges: Int? = null,
    val step: Int? = null
)

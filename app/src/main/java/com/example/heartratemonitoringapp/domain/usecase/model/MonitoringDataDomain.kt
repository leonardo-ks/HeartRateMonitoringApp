package com.example.heartratemonitoringapp.domain.usecase.model

data class MonitoringDataDomain(
    val avgHeartRate: String? = null,
    val userId: Int? = null,
    val createdAt: String? = null,
    val id: Int? = null,
    val label: String? = null,
    val avgStep: String? = null
)

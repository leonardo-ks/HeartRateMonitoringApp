package com.example.core.data.source.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitoring_data")
data class MonitoringDataEntities(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: Int?,
    @ColumnInfo(name = "user_id")
    val userId: Int?,
    @ColumnInfo(name = "avg_heart_rate")
    val avgHeartRate: Int?,
    @ColumnInfo(name = "step_changes")
    val stepChanges: Int?,
    @ColumnInfo(name = "step")
    val step: Int?,
    @ColumnInfo(name = "label")
    val label: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?
)

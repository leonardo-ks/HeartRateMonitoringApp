package com.example.heartratemonitoringapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MonitoringData(

    @field:SerializedName("avg_heart_rate")
    val avgHeartRate: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("label")
    val label: String? = null,

    @field:SerializedName("step_changes")
    val stepChanges: Int? = null,

    @field:SerializedName("step")
    val step: Int? = null
)

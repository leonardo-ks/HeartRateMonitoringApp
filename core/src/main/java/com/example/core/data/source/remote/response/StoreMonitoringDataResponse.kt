package com.example.core.data.source.remote.response

import com.example.core.data.source.remote.response.MonitoringData
import com.google.gson.annotations.SerializedName

data class StoreMonitoringDataResponse(

    @field:SerializedName("data")
	val data: MonitoringData? = null,

    @field:SerializedName("success")
	val success: Boolean = false,

    @field:SerializedName("message")
	val message: String? = null
)

package com.example.heartratemonitoringapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserMonitoringDataResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("data")
	val data: List<MonitoringData?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

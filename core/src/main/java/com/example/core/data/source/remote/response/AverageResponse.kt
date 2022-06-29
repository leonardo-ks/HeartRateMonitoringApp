package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class AverageResponse(

	@field:SerializedName("avg_heart_rate")
	val avgHeartRate: Int? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("today_steps")
	val todaySteps: Int? = null
)
package com.example.heartratemonitoringapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class FindDataResponse(

	@field:SerializedName("found")
	val found: Boolean? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("labels")
	val labels: List<String>? = null
)

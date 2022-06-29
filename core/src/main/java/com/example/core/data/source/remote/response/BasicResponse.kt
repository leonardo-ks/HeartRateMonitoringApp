package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class BasicResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("data")
	val profile: UserData? = null,

	@field:SerializedName("message")
	val message: String? = null
)

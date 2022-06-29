package com.example.heartratemonitoringapp.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserDataUpdateResponse(

	@field:SerializedName("data")
	val data: UserData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

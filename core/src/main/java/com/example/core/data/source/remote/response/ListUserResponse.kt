package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListUserResponse(

    @field:SerializedName("success")
	val success: Boolean? = null,

    @field:SerializedName("data")
	val data: List<UserData?>? = null,

    @field:SerializedName("message")
	val message: String? = null
)

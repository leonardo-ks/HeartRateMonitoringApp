package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LimitResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("lower")
	val lower: Int? = null,

	@field:SerializedName("upperStill")
	val upperStill: Int? = null,

	@field:SerializedName("upperWalk")
	val upperWalk: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)
